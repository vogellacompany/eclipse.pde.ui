/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.usage;

import junit.framework.Test;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.pde.api.tools.internal.problems.ApiProblemFactory;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;

/**
 * Tests a variety of class usages where the callee has API restrictions
 * 
 * @since 1.0
 */
public class ClassUsageTests extends UsageTest {

	protected static final String CLASS_NAME = "ClassUsageClass";
	
	
	/**
	 * Constructor
	 * @param name
	 */
	public ClassUsageTests(String name) {
		super(name);
	}

	/**
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#getDefaultProblemId()
	 */
	@Override
	protected int getDefaultProblemId() {
		return 0;
	}

	/**
	 * Returns the problem id with the given kind
	 * @param kind
	 * @return the problem id
	 */
	protected int getProblemId(int kind, int flags) {
		return ApiProblemFactory.createProblemId(
				IApiProblem.CATEGORY_USAGE, 
				IElementDescriptor.TYPE, 
				kind, 
				flags);
	}
	
	public static Test suite() {
		return buildTestSuite(ClassUsageTests.class);
	}
	
	/**
	 * @see org.eclipse.pde.api.tools.builder.tests.usage.UsageTest#getTestSourcePath()
	 */
	@Override
	protected IPath getTestSourcePath() {
		return super.getTestSourcePath().append("class");
	}
	
	/**
	 * Tests that classes the extend a restricted class are properly flagged 
	 * using a full build
	 */
	public void testClassUsageTests1F() {
		x1(false);
	}
	
	/**
	 * Tests the classes the extend a restricted class are properly flagged
	 * using an incremental build
	 */
	public void testClassUsageTests1I() {
		x1(true);
	}
	
	private void x1(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.NO_FLAGS)
		});
		String typename = "testC1";
		setExpectedMessageArgs(new String[][] {
				{CLASS_NAME, typename},
				{CLASS_NAME, INNER_NAME1},
				{CLASS_NAME, INNER_NAME2},
				{CLASS_NAME, OUTER_NAME}
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests that classes the instantiate a restricted class are properly flagged 
	 * using a full build
	 */
	public void testClassUsageTests2F() {
		x2(false);
	}
	
	/**
	 * Tests the classes the instantiate a restricted class are properly flagged
	 * using an incremental build
	 */
	public void testClassUsageTests2I() {
		x2(true);
	}
	
	private void x2(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS)
		});
		String typename = "testC2";
		setExpectedMessageArgs(new String[][] {
				{CLASS_NAME, typename},
				{CLASS_NAME, INNER_NAME1},
				{CLASS_NAME, INNER_NAME2},
				{CLASS_NAME, OUTER_NAME}
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests that indirect illegal implementing is properly 
	 * detected for one class and an extension interface using a full build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements1F() {
		x3(false);
	}
	
	/**
	 * Tests that indirect illegal implementing is properly 
	 * detected for one class and an extension interface using an incremental build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements1I() {
		x3(true);
	}
	
	private void x3(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_IMPLEMENT, IApiProblem.INDIRECT_REFERENCE)
		});
		String typename = "testC3";
		setExpectedMessageArgs(new String[][] {
				{"IExtInterface1", "INoImpl1", typename},
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests that an indirect illegal implement is ignored when there is a
	 * parent class that implements the @noimplement interface using 
	 * a full build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements2F() {
		x4(false);
	}
	
	/**
	 * Tests that an indirect illegal implement is ignored when there is a
	 * parent class that implements the @noimplement interface using 
	 * an incremental build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements2I() {
		x4(true);
	}
	
	private void x4(boolean inc) {
		expectingNoProblems();
		deployUsageTest("testC4", inc);
	}
	
	/**
	 * Tests that multiple indirect illegal implements are detected when there is no
	 * parent class that implements the @noimplement interfaces using 
	 * a full build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements3F() {
		x5(false);
	}
	
	/**
	 * Tests that multiple indirect illegal implements are detected when there is no
	 * parent class that implements the @noimplement interfaces using 
	 * an incremental build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements3I() {
		x5(true);
	}
	
	private void x5(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_IMPLEMENT, IApiProblem.INDIRECT_REFERENCE),
				getProblemId(IApiProblem.ILLEGAL_IMPLEMENT, IApiProblem.INDIRECT_REFERENCE),
				getProblemId(IApiProblem.ILLEGAL_IMPLEMENT, IApiProblem.INDIRECT_REFERENCE),
				getProblemId(IApiProblem.ILLEGAL_IMPLEMENT, IApiProblem.INDIRECT_REFERENCE)
		});
		String typename = "testC5";
		setExpectedMessageArgs(new String[][] {
				{"IExtInterface1", "INoImpl1", typename},
				{"IExtInterface2", "INoImpl1", typename},
				{"IExtInterface3", "INoImpl1", typename},
				{"IExtInterface4", "INoImpl4", typename}
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests that multiple indirect illegal implements are detected when there is a
	 * parent class that implements the @noimplement interfaces using 
	 * a full build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements4F() {
		x6(false);
	}
	
	/**
	 * Tests that multiple indirect illegal implements are detected when there is a
	 * parent class that implements the @noimplement interfaces using 
	 * an incremental build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements4I() {
		x6(true);
	}
	
	private void x6(boolean inc) {
		expectingNoProblems();
		deployUsageTest("testC6", inc);
	}
	
	/**
	 * Tests that an indirect illegal implements is detected when there is a
	 * parent class N levels indirected that implements the @noimplement interfaces using 
	 * a full build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements5F() {
		x7(false);
	}
	
	/**
	 * Tests that an indirect illegal implements is detected when there is a
	 * parent class N levels indirected that implements the @noimplement interfaces using 
	 * an incremental build
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
	 */
	public void testClassIndirectImplements5I() {
		x7(true);
	}
	
	private void x7(boolean inc) {
		expectingNoProblems();
		deployUsageTest("testC7", inc);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=255804
	 */
	public void testClassIndirectImplements6F() {
		x8(false);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=255804
	 */
	public void testClassIndirectImplements6I() {
		x8(true);
	}
	
	private void x8(boolean inc) {
		expectingNoProblems();
		deployUsageTest("testC8", inc);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=255804
	 */
	public void testClassIndirectImplements7F() {
		x9(false);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=255804
	 */
	public void testClassIndirectImplements7I() {
		x9(true);
	}
	
	private void x9(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_IMPLEMENT, IApiProblem.NO_FLAGS)
		});
		String typename = "testC9";
		setExpectedMessageArgs(new String[][] {
				{"INoImpl1", typename}
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests an anonymous type defined in the return statement of a method illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends2F() {
		x11(false);
	}
	
	/**
	 * Tests an anonymous type defined in the return statement of a method illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends2I() {
		x11(true);
	}
	
	private void x11(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.ANONYMOUS_TYPE)
		});
		String typename = "testA2";
		setExpectedMessageArgs(new String[][] {
				{"x.y.z.testA2.m1()", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests an anonymous type defined in a method field illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends3F() {
		x12(false);
	}
	
	/**
	 * Tests an anonymous type defined in a method field illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends3I() {
		x12(true);
	}
	
	private void x12(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.ANONYMOUS_TYPE)
		});
		String typename = "testA3";
		setExpectedMessageArgs(new String[][] {
				{"x.y.z.testA3.m1()", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests an anonymous type defined in a class field illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends4F() {
		x13(false);
	}
	
	/**
	 * Tests an anonymous type defined in a method field illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends4I() {
		x13(true);
	}
	
	private void x13(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.ANONYMOUS_TYPE)
		});
		String typename = "testA4";
		setExpectedMessageArgs(new String[][] {
				{"x.y.z.testA4", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests a local anonymous field defined in a constructor illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends6F() {
		x15(false);
	}
	
	/**
	 * Tests a local anonymous field defined in a constructor illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends6I() {
		x15(true);
	}
	
	private void x15(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.ANONYMOUS_TYPE)
		});
		String typename = "testA6";
		setExpectedMessageArgs(new String[][] {
				{"x.y.z.testA6.testA6()", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests an anonymous type defined in a static initializer illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends7F() {
		x18(false);
	}
	
	/**
	 * Tests an anonymous type defined in a static initializer illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testAnonymousClassExtends7I() {
		x18(true);
	}
	
	private void x18(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.ANONYMOUS_TYPE)
		});
		String typename = "testA11";
		setExpectedMessageArgs(new String[][] {
				{"x.y.z.testA11", CLASS_NAME}
		});
		deployUsageTest(typename, inc);
	}

	public void testLocalClassExtends1F() {
		x16(false);
	}
	
	public void testLocalClassExtends1I() {
		x16(true);
	}
	
	/**
	 * Tests that local types with the same name in different methods are correctly found
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=258101
	 * @param inc
	 */
	private void x16(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.LOCAL_TYPE),
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.LOCAL_TYPE),
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.LOCAL_TYPE)
		});
		getEnv().getJavaProject(getTestingProjectName()).getOption(JavaCore.COMPILER_COMPLIANCE, true);
		setExpectedMessageArgs(new String[][] {
				{"inner", "x.y.z.testA7.m1()", CLASS_NAME},
				{"inner", "x.y.z.testA7.m2()", CLASS_NAME},
				{"inner", "x.y.z.testA7.m3()", CLASS_NAME}
		});
		setExpectedLineMappings(new LineMapping[] {
				new LineMapping(21, getExpectedProblemIds()[0], getExpectedMessageArgs()[0]),
				new LineMapping(28, getExpectedProblemIds()[1], getExpectedMessageArgs()[1]),
				new LineMapping(35, getExpectedProblemIds()[2], getExpectedMessageArgs()[2])
		});
		String typename = "testA7";
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests a local type defined in a constructor illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testLocalClassExtends2F() {
		x14(false);
	}
	
	/**
	 * Tests a local type defined in a constructor illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testLocalClassExtends2I() {
		x14(true);
	}
	
	private void x14(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.LOCAL_TYPE)
		});
		String typename = "testA5";
		setExpectedMessageArgs(new String[][] {
				{"inner", "x.y.z.testA5.testA5()", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests a local type defined in a constructor illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testLocalClassExtends3F() {
		x17(false);
	}
	
	/**
	 * Tests a local type defined in a constructor illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testLocalClassExtends3I() {
		x17(true);
	}
	
	private void x17(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.LOCAL_TYPE)
		});
		String typename = "testA8";
		setExpectedMessageArgs(new String[][] {
				{"inner", "x.y.z.testA8.m1()", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
	
	/**
	 * Tests a local type defined in a method illegally extending a
	 * restricted type using a full build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testLocalClassExtends4F() {
		x10(false);
	}
	
	/**
	 * Tests a local type defined in a method illegally extending a
	 * restricted type using an incremental build.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246672
	 */
	public void testLocalClassExtends4I() {
		x10(true);
	}
	
	private void x10(boolean inc) {
		setExpectedProblemIds(new int[] {
				getProblemId(IApiProblem.ILLEGAL_EXTEND, IApiProblem.LOCAL_TYPE)
		});
		String typename = "testA1";
		setExpectedMessageArgs(new String[][] {
				{"inner", "x.y.z.testA1.m1()", CLASS_NAME}	
		});
		deployUsageTest(typename, inc);
	}
}
