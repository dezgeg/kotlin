/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.resolve.constraintSystem

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.intellij.psi.PsiElement
import org.jetbrains.jet.ConfigurationKind
import org.jetbrains.jet.JetLiteFixture
import org.jetbrains.jet.JetTestUtils
import org.jetbrains.jet.analyzer.AnalyzeExhaust
import org.jetbrains.jet.cli.jvm.compiler.JetCoreEnvironment
import org.jetbrains.jet.di.InjectorForTests
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor
import org.jetbrains.jet.lang.descriptors.SimpleFunctionDescriptor
import org.jetbrains.jet.lang.descriptors.TypeParameterDescriptor
import org.jetbrains.jet.lang.diagnostics.DiagnosticFactory
import org.jetbrains.jet.lang.diagnostics.rendering.Renderers
import org.jetbrains.jet.lang.psi.*
import org.jetbrains.jet.lang.resolve.*
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintPosition
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintSystemImpl
import org.jetbrains.jet.lang.resolve.java.AnalyzerFacadeForJVM
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.lang.types.JetType
import org.jetbrains.jet.lang.types.Variance
import org.jetbrains.jet.lang.types.expressions.ExpressionTypingServices
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns
import java.io.File
import java.io.IOException
import java.util.Collections
import com.intellij.openapi.project.Project
import java.util.HashMap
import java.util.regex.Pattern
import org.jetbrains.jet.resolve.constraintSystem.ConstraintSystemTest.MyConstraintKind
import org.jetbrains.jet.resolve.constraintSystem.ConstraintSystemTest.MyConstraint
import java.util.ArrayList
import kotlin.test.assertEquals
import org.junit.Assert

public class ConstraintSystemTest() : JetLiteFixture() {

    val constraintPattern = Pattern.compile("(SUBTYPE|SUPERTYPE|EQUALITY)\\s+(\\w+)\\s+(\\w+)", Pattern.MULTILINE)
    val variablesPattern = Pattern.compile("VARIABLES\\s+(.*)")

    private var builtIns: KotlinBuiltIns? = null
    private var typeResolver: TypeResolver? = null
    private var expressionTypingServices: ExpressionTypingServices? = null
    private var functionDescriptor: FunctionDescriptor? = null
    private var scopeToResolveTypeParameters: JetScope? = null

    override fun createEnvironment(): JetCoreEnvironment {
        return createEnvironmentWithMockJdk(ConfigurationKind.ALL)
    }

    override fun setUp() {
        super.setUp()

        builtIns = KotlinBuiltIns.getInstance()

        val injector = InjectorForTests(getProject(), JetTestUtils.createEmptyModule()!!)
        typeResolver = injector.getTypeResolver()
        expressionTypingServices = injector.getExpressionTypingServices()

        analyzeDeclarations()
    }

    override fun getTestDataPath(): String {
        return super.getTestDataPath() + "/constraintSystem/"
    }

    private fun analyzeDeclarations(): MyDeclarations {
        val fileName = "declarations/declarations.kt"

        val psiFile = createPsiFile(null, fileName, loadFile(fileName))
        val analyzeExhaust = AnalyzerFacadeForJVM.analyzeOneFileWithJavaIntegration(psiFile, Collections.emptyList<AnalyzerScriptParameter>())!!
        val bindingContext = analyzeExhaust.getBindingContext()
        return MyDeclarations(bindingContext, getProject(), typeResolver!!)
    }

    public fun testSimpleSuccessful() {
        doTest("compiler/testData/constraintSystem/simpleSuccessful.bounds")
    }

    public fun doTest(filePath: String) {
        val file = File(filePath)
        val fileText = JetTestUtils.doLoadFile(file)!!

        val constraintSystem = ConstraintSystemImpl()
        val myDeclarations = analyzeDeclarations()

        val typeParameterDescriptors = HashMap<TypeParameterDescriptor, Variance>()
        val variables = parseVariables(fileText)
        for (variable in variables) {
            typeParameterDescriptors.put(myDeclarations.getParameterDescriptor(variable), Variance.INVARIANT)
        }
        constraintSystem.registerTypeVariables(typeParameterDescriptors)

        val constraints = parseConstraints(fileText)
        for (constraint in constraints) {
            val firstType = myDeclarations.getType(constraint.firstType)
            val secondType = myDeclarations.getType(constraint.secondType)
            val position = if (constraint.isWeak) ConstraintPosition.getTypeBoundPosition(0)!! else ConstraintPosition.SPECIAL
            when (constraint.kind) {
                MyConstraintKind.SUBTYPE -> constraintSystem.addSubtypeConstraint(firstType, secondType, position)
                MyConstraintKind.SUPERTYPE -> constraintSystem.addSupertypeConstraint(firstType, secondType, position)
            }
        }

        val status = parseStatus(fileText)
        val result = Renderers.RENDER_CONSTRAINT_SYSTEM.render(constraintSystem)
        Assert.assertEquals("Constraint system status mismatch", status, result)
    }

    class MyConstraint(val kind: MyConstraintKind, val firstType: String, val secondType: String, val isWeak: Boolean)
    enum class MyConstraintKind {
        SUBTYPE SUPERTYPE
    }

    private fun parseVariables(text: String): List<String> {
        val matcher = variablesPattern.matcher(text)
        if (matcher.find()) {
            val variablesText = matcher.group(1)!!
            val variables = variablesText.split(' ')
            return variables.toList()
        }
        throw AssertionError("Type variable names should be declared")
    }

    private fun parseConstraints(text: String): List<MyConstraint> {
        val constraints = ArrayList<MyConstraint>()
        val matcher = constraintPattern.matcher(text)
        while (matcher.find()) {
            val kind = MyConstraintKind.valueOf(matcher.group(1)!!)
            val firstType = matcher.group(2)!!
            val secondType = matcher.group(3)!!
            val isWeak = matcher.groupCount() == 4 && matcher.group(4) == "!"
            constraints.add(MyConstraint(kind, firstType, secondType, isWeak))
        }
        return constraints
    }

    private fun parseStatus(text: String) = text.substring(text.indexOf("type parameter bounds"), text.length())
}



