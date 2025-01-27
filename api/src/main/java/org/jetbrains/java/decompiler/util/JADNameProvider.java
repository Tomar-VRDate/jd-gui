/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.java.decompiler.util;

import org.jetbrains.java.decompiler.code.CodeConstants;
import org.jetbrains.java.decompiler.main.extern.IVariableNameProvider;
import org.jetbrains.java.decompiler.main.extern.IVariableNamingFactory;
import org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionPair;
import org.jetbrains.java.decompiler.struct.StructMethod;
import org.jetbrains.java.decompiler.struct.gen.MethodDescriptor;
import org.jetbrains.java.decompiler.struct.gen.VarType;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class JADNameProvider
				implements IVariableNameProvider {
	private static final Pattern                 CAPS_START       = Pattern.compile("^[A-Z]");
	private static final Pattern                 ARRAY            = Pattern.compile("(\\[|\\.\\.\\.)");
	private final        boolean                 renameParameters = false;
	private              HashMap<String, Holder> last             = null;
	private              HashMap<String, String> remap            = null;
	private              StructMethod            method           = null;

	public JADNameProvider(StructMethod wrapper) {
		last = new HashMap<String, Holder>();
		last.put("int",
		         new Holder(0,
		                    true,
		                    "i",
		                    "j",
		                    "k",
		                    "l"));
		last.put("byte",
		         new Holder(0,
		                    false,
		                    "b"));
		last.put("char",
		         new Holder(0,
		                    false,
		                    "c"));
		last.put("short",
		         new Holder(1,
		                    false,
		                    "short"));
		last.put("boolean",
		         new Holder(0,
		                    true,
		                    "flag"));
		last.put("double",
		         new Holder(0,
		                    false,
		                    "d"));
		last.put("float",
		         new Holder(0,
		                    true,
		                    "f"));
		last.put("File",
		         new Holder(1,
		                    true,
		                    "file"));
		last.put("String",
		         new Holder(0,
		                    true,
		                    "s"));
		last.put("Class",
		         new Holder(0,
		                    true,
		                    "oclass"));
		last.put("Long",
		         new Holder(0,
		                    true,
		                    "olong"));
		last.put("Byte",
		         new Holder(0,
		                    true,
		                    "obyte"));
		last.put("Short",
		         new Holder(0,
		                    true,
		                    "oshort"));
		last.put("Boolean",
		         new Holder(0,
		                    true,
		                    "obool"));
		last.put("Package",
		         new Holder(0,
		                    true,
		                    "opackage"));
		last.put("Enum",
		         new Holder(0,
		                    true,
		                    "oenum"));

		remap = new HashMap<String, String>();
		remap.put("long",
		          "int");

		this.method = wrapper;
	}

	@Override
	public synchronized void addParentContext(IVariableNameProvider iparent) {
		JADNameProvider         parent = (JADNameProvider) iparent;
		HashMap<String, Holder> temp   = new HashMap<String, Holder>();
		for (Entry<String, Holder> e : parent.last.entrySet()) {
			Holder v = e.getValue();
			temp.put(e.getKey(),
			         new Holder(v.id,
			                    v.skip_zero,
			                    v.names));
		}
		this.last = temp;
		this.remap = new HashMap<>(parent.remap);
	}

	@Override
	public Map<VarVersionPair, String> rename(Map<VarVersionPair, String> entries) {
		int params = 0;
		if ((this.method.getAccessFlags() & CodeConstants.ACC_STATIC) != CodeConstants.ACC_STATIC) {
			params++;
		}

		MethodDescriptor md = MethodDescriptor.parseDescriptor(this.method.getDescriptor());
		for (VarType param : md.params) {
			params += param.stackSize;
		}

		List<VarVersionPair> keys = new ArrayList<VarVersionPair>(entries.keySet());
		Collections.sort(keys,
		                 new Comparator<VarVersionPair>() {
			                 @Override
			                 public int compare(VarVersionPair o1,
			                                    VarVersionPair o2) {
				                 if (o1.var != o2.var) {
					                 return o1.var - o2.var;
				                 }
				                 return o1.version - o2.version;
			                 }
		                 });

		Map<VarVersionPair, String> result = new LinkedHashMap<VarVersionPair, String>();
		for (VarVersionPair ver : keys) {
			String type = entries.get(ver);
			if ("this".equals(type)) {
				continue;
			}
			if (type.indexOf('<') != -1) {
				type = type.substring(0,
				                      type.indexOf('<'));
			}
			if (type.indexOf('.') != -1) {
				type = type.substring(type.lastIndexOf('.') + 1);
			}
			if (renameParameters || ver.var >= params) {
				result.put(ver,
				           getNewName(type));
			}
		}
		return result;
	}

	protected synchronized String getNewName(String type) {
		String index    = null;
		String findtype = type;

		while (findtype.contains("[][]")) {
			findtype = findtype.replaceAll("\\[\\]\\[\\]",
			                               "[]");
		}
		if (last.containsKey(findtype)) {
			index = findtype;
		} else if (last.containsKey(findtype.toLowerCase(Locale.ENGLISH))) {
			index = findtype.toLowerCase(Locale.ENGLISH);
		} else if (remap.containsKey(type)) {
			index = remap.get(type);
		}

		if ((index == null || index.length() == 0) && (CAPS_START.matcher(type)
		                                                         .find() || ARRAY.matcher(type)
		                                                                         .find())) { // replace multi things with
			// arrays.
			type = type.replace("...",
			                    "[]");

			while (type.contains("[][]")) {
				type = type.replaceAll("\\[\\]\\[\\]",
				                       "[]");
			}

			String name = type.toLowerCase(Locale.ENGLISH);
			// Strip single dots that might happen because of inner class references
			name = name.replace(".",
			                    "");
			boolean skip_zero = true;

			if (Pattern.compile("\\[")
			           .matcher(type)
			           .find()) {
				skip_zero = true;
				name = "a" + name.replace("[]",
				                          "")
				                 .replace("...",
				                          "");
			}

			last.put(type.toLowerCase(Locale.ENGLISH),
			         new Holder(0,
			                    skip_zero,
			                    name));
			index = type.toLowerCase(Locale.ENGLISH);
		}

		if (index == null || index.length() == 0) {
			return type.toLowerCase(Locale.ENGLISH);
		}

		Holder       holder = last.get(index);
		int          id     = holder.id;
		List<String> names  = holder.names;

		int ammount = names.size();

		String name;
		if (ammount == 1) {
			name = names.get(0) + (id == 0 && holder.skip_zero
			                       ? ""
			                       : id);
		} else {
			int num = id / ammount;
			name = names.get(id % ammount) + (id < ammount && holder.skip_zero
			                                  ? ""
			                                  : num);
		}

		holder.id++;
		return name;
	}

	@Override
	public String renameAbstractParameter(String abstractParam,
	                                      int index) {
		return abstractParam;
	}

	private static class Holder {
		public final List<String> names = new ArrayList<String>();
		public       int          id;
		public       boolean      skip_zero;

		public Holder(int t1,
		              boolean skip_zero,
		              String... names) {
			this.id = t1;
			this.skip_zero = skip_zero;
			Collections.addAll(this.names,
			                   names);
		}

		public Holder(int t1,
		              boolean skip_zero,
		              List<String> names) {
			this.id = t1;
			this.skip_zero = skip_zero;
			this.names.addAll(names);
		}
	}

	public static class JADNameProviderFactory
					implements IVariableNamingFactory {
		@Override
		public IVariableNameProvider createFactory(StructMethod method) {
			return new JADNameProvider(method);
		}
	}
}
