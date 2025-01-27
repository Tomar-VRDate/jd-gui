// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be
// found in the LICENSE file.
package org.jetbrains.java.decompiler.modules.decompiler;

import org.jetbrains.java.decompiler.modules.decompiler.stats.DoStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;
import org.jetbrains.java.decompiler.struct.StructClass;

import java.util.*;


public class EliminateLoopsHelper {

	public static boolean eliminateLoops(Statement root,
	                                     StructClass cl) {

		boolean ret = eliminateLoopsRec(root);

		if (ret) {
			SequenceHelper.condenseSequences(root);

			Set<Integer> setReorderedIfs = new HashSet<>();

			SimplifyExprentsHelper sehelper = new SimplifyExprentsHelper(false);
			// TODO: what problems does SSA being null cause?
			while (sehelper.simplifyStackVarsStatement(root,
			                                           setReorderedIfs,
			                                           null,
			                                           cl)) {
				SequenceHelper.condenseSequences(root);
			}
		}

		return ret;
	}

	private static boolean eliminateLoopsRec(Statement stat) {

		for (Statement st : stat.getStats()) {
			if (eliminateLoopsRec(st)) {
				return true;
			}
		}

		return stat.type == Statement.TYPE_DO && isLoopRedundant((DoStatement) stat);
	}

	private static boolean isLoopRedundant(DoStatement loop) {

		if (loop.getLooptype() != DoStatement.LOOP_DO) {
			return false;
		}

		// get parent loop if exists
		Statement parentloop = loop.getParent();
		while (parentloop != null && parentloop.type != Statement.TYPE_DO) {
			parentloop = parentloop.getParent();
		}

		if (parentloop == null || parentloop.getBasichead() != loop.getBasichead()) {
			return false;
		}

		// collect relevant break edges
		List<StatEdge> lstBreakEdges = new ArrayList<>();
		for (StatEdge edge : loop.getLabelEdges()) {
			if (edge.getType() == StatEdge.TYPE_BREAK) { // all break edges are explicit because of LOOP_DO type
				lstBreakEdges.add(edge);
			}
		}


		Statement loopcontent = loop.getFirst();

		boolean firstok = loopcontent.getAllSuccessorEdges()
		                             .isEmpty();
		if (!firstok) {
			StatEdge edge = loopcontent.getAllSuccessorEdges()
			                           .get(0);
			firstok = (edge.closure == loop && edge.getType() == StatEdge.TYPE_BREAK);
			if (firstok) {
				lstBreakEdges.remove(edge);
			}
		}


		if (!lstBreakEdges.isEmpty()) {
			if (firstok) {

				HashMap<Integer, Boolean> statLabeled     = new HashMap<>();
				List<Statement>           lstEdgeClosures = new ArrayList<>();

				for (StatEdge edge : lstBreakEdges) {
					Statement minclosure = LowBreakHelper.getMinClosure(loopcontent,
					                                                    edge.getSource());
					lstEdgeClosures.add(minclosure);
				}

				int precount = loop.isLabeled()
				               ? 1
				               : 0;
				for (Statement st : lstEdgeClosures) {
					if (!statLabeled.containsKey(st.id)) {
						boolean btemp = st.isLabeled();
						precount += btemp
						            ? 1
						            : 0;
						statLabeled.put(st.id,
						                btemp);
					}
				}

				for (int i = 0;
				     i < lstBreakEdges.size();
				     i++) {
					Statement st = lstEdgeClosures.get(i);
					statLabeled.put(st.id,
					                LowBreakHelper.isBreakEdgeLabeled(lstBreakEdges.get(i)
					                                                               .getSource(),
					                                                  st) | statLabeled.get(st.id));
				}

				for (int i = 0;
				     i < lstBreakEdges.size();
				     i++) {
					lstEdgeClosures.set(i,
					                    getMaxBreakLift(lstEdgeClosures.get(i),
					                                    lstBreakEdges.get(i),
					                                    statLabeled,
					                                    loop));
				}

				statLabeled.clear();
				for (Statement st : lstEdgeClosures) {
					statLabeled.put(st.id,
					                st.isLabeled());
				}

				for (int i = 0;
				     i < lstBreakEdges.size();
				     i++) {
					Statement st = lstEdgeClosures.get(i);
					statLabeled.put(st.id,
					                LowBreakHelper.isBreakEdgeLabeled(lstBreakEdges.get(i)
					                                                               .getSource(),
					                                                  st) | statLabeled.get(st.id));
				}

				long postcount = statLabeled.values()
				                            .stream()
				                            .filter(Boolean::booleanValue)
				                            .count();

				if (precount <= postcount) {
					return false;
				} else {
					for (int i = 0;
					     i < lstBreakEdges.size();
					     i++) {
						lstEdgeClosures.get(i)
						               .addLabeledEdge(lstBreakEdges.get(i));
					}
				}
			} else {
				return false;
			}
		}

		eliminateLoop(loop,
		              parentloop);

		return true;
	}

	private static Statement getMaxBreakLift(Statement stat,
	                                         StatEdge edge,
	                                         HashMap<Integer, Boolean> statLabeled,
	                                         Statement max) {

		Statement closure    = stat;
		Statement newclosure = stat;

		while ((newclosure = getNextBreakLift(newclosure,
		                                      edge,
		                                      statLabeled,
		                                      max)) != null) {
			closure = newclosure;
		}

		return closure;
	}

	private static Statement getNextBreakLift(Statement stat,
	                                          StatEdge edge,
	                                          HashMap<Integer, Boolean> statLabeled,
	                                          Statement max) {

		Statement closure = stat.getParent();

		while (closure != null && closure != max && !closure.containsStatementStrict(edge.getDestination())) {

			boolean edge_labeled = LowBreakHelper.isBreakEdgeLabeled(edge.getSource(),
			                                                         closure);
			boolean stat_labeled = statLabeled.containsKey(closure.id)
			                       ? statLabeled.get(closure.id)
			                       : closure.isLabeled();

			if (stat_labeled || !edge_labeled) {
				return closure;
			}

			closure = closure.getParent();
		}

		return null;
	}

	private static void eliminateLoop(Statement loop,
	                                  Statement parentloop) {

		// move continue edges to the parent loop
		List<StatEdge> lst = new ArrayList<>(loop.getLabelEdges());
		for (StatEdge edge : lst) {
			loop.removePredecessor(edge);
			edge.getSource()
			    .changeEdgeNode(Statement.DIRECTION_FORWARD,
			                    edge,
			                    parentloop);
			parentloop.addPredecessor(edge);

			parentloop.addLabeledEdge(edge);
		}

		// remove the last break edge, if exists
		Statement loopcontent = loop.getFirst();
		// TODO: reimplement this properly
		//    if (!loopcontent.getAllSuccessorEdges().isEmpty()) {
		//      loopcontent.removeSuccessor(loopcontent.getAllSuccessorEdges().get(0));
		//    }

		// replace loop with its content
		loop.getParent()
		    .replaceStatement(loop,
		                      loopcontent);
	}
}
