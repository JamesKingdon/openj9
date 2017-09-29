/*[INCLUDE-IF Sidecar16]*/
package com.ibm.oti.vm;

import java.util.Properties;

/*[IF Sidecar19-SE]*/
import jdk.internal.reflect.ConstantPool;
/*[ELSE]*/
import sun.reflect.ConstantPool;
/*[ENDIF]*/


/*******************************************************************************
 * Copyright (c) 2012, 2017 IBM Corp. and others
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution and is available at https://www.eclipse.org/legal/epl-2.0/
 * or the Apache License, Version 2.0 which accompanies this distribution and
 * is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This Source Code may also be made available under the following
 * Secondary Licenses when the conditions for such availability set
 * forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
 * General Public License, version 2 with the GNU Classpath
 * Exception [1] and GNU General Public License, version 2 with the
 * OpenJDK Assembly Exception [2].
 *
 * [1] https://www.gnu.org/software/classpath/license.html
 * [2] http://openjdk.java.net/legal/assembly-exception.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

/**
 * Interface to allow privileged access to classes
 * from outside the java.lang package. Based on sun.misc.SharedSecrets
 * implementation.
 */
public interface VMLangAccess {
/*[IF Sidecar19-SE]*/
	/**
	 * Answer the platform class loader.
	 */
	public ClassLoader getPlatformClassLoader();
/*[ENDIF] Sidecar19-SE*/

	/**
	 * Uses native to find and load a class using the VM
	 *
	 * @return 		java.lang.Class
	 *					the class or null.
	 * @param 		className String
	 *					the name of the class to search for.
	 * @param		classLoader
	 *					the classloader to do the work
	 */
	public Class<?> findClassOrNullHelper(String className, ClassLoader classLoader);
	
	/**
	 * Answer the extension class loader.
	 */
	public ClassLoader getExtClassLoader();

	/**
	 * Returns true if parent is the ancestor of child.
	 * Parent and child must not be null.
	 */
	/*[PR CMVC 191554] Provide access to ClassLoader methods to improve performance */
	public boolean isAncestor(java.lang.ClassLoader parent, java.lang.ClassLoader child);
	
	/**
	 * Returns the ClassLoader off clazz.
	 */
	/*[PR CMVC 191554] Provide access to ClassLoader methods to improve performance */
	public java.lang.ClassLoader getClassloader(java.lang.Class clazz);
	
	/**
	 * Returns the package name for a given class.
	 */
	/*[PR CMVC 191554] Provide access to ClassLoader methods to improve performance */
	public java.lang.String getPackageName(java.lang.Class clazz);

	/**
	 * Returns a MethodHandle cache for a given class.
	 */
	public java.lang.Object getMethodHandleCache(java.lang.Class<?> clazz);
	
	/**
	 * Set a MethodHandle cache to a given class.
	 */
	public java.lang.Object setMethodHandleCache(java.lang.Class<?> clazz, java.lang.Object object);
	
	/**
	 * Returns a {@code java.util.Map} from method descriptor string to the equivalent {@code MethodType} as generated by {@code MethodType.fromMethodDescriptorString}.
	 * @param loader The {@code ClassLoader} used to get the MethodType.
	 * @return A {@code java.util.Map} from method descriptor string to the equivalent {@code MethodType}.
	 */
	public java.util.Map<String, java.lang.invoke.MethodType> getMethodTypeCache(ClassLoader loader);
	
	/**
	 *	Provide internal access to the system properties without going through SecurityManager
	 *
	 *  Important notes: 
	 *  	1. This API must NOT be exposed to application code directly or indirectly;
	 *  	2. This method can only be used to retrieve system properties for internal usage,
	 *  		i.e., there is no security exception expected;
	 *  	3. If there is an application caller in the call stack, AND the application caller(s) 
	 *  		have to be check for permission to retrieve the system properties specified,
	 *  		then this API should NOT be used even though the immediate caller is in boot strap path.
	 *   
	 * @return the system properties
	 */
	public Properties internalGetProperties();
	
	
	/*[IF !Sidecar19-SE]*/
	/**
	 * Returns the system packages for the bootloader
	 * @return An array of packages defined by the bootloader
	 */
	public Package[] getSystemPackages();
	
	/**
	 * Returns the system package for the 'name'
	 * @param name must not be null
	 * @return The package
	 */
	public Package getSystemPackage(String name);
	/*[ENDIF]*/
	
	/**
	 * Returns an InternalRamClass object.
	 * 
	 * @param addr - the native addr of the J9Class
	 * @return An InternalRamClass object
	 */ 
	public Object createInternalRamClass(long addr);
	
	/**
	 * Returns a ConstanPool object
	 * 
	 * @param internalRamClass An object ref to an internalRamClass
	 * @return ContanstPool instance
	 */
	public ConstantPool getConstantPool(Object internalRamClass);
}
