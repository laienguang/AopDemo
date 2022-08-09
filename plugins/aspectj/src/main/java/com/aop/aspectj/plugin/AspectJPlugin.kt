package com.aop.aspectj.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import org.apache.tools.ant.util.StringUtils
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import java.io.File

class AspectJPlugin : Plugin<Project> {

    private fun doLastOnVariant(variant: BaseVariant, extension: TestedExtension, logger: Logger, project: Project) {
        val javaCompile = variant.javaCompileProvider.get()
        if (!variant.buildType.isDebuggable) {
            logger.warn("Skipping non-debuggable build type '${variant.buildType.name}'.")
            return
        }
        variant.outputs.all { output ->
            var fullName = ""
            output.name.split('-').filter { it.isNotEmpty() }.forEachIndexed { index, token ->
                fullName += if (index == 0) token else token.capitalize()
            }
            javaCompile.doLast {
                val args = arrayOf("-showWeaveInfo",
                    "-1.8",
                    "-inpath", javaCompile.destinationDir.toString(),
                    "-aspectpath", javaCompile.classpath.asPath,
                    "-d", javaCompile.destinationDir.toString(),
                    "-classpath", javaCompile.classpath.asPath,
                    "-bootclasspath", StringUtils.join(extension.bootClasspath.map {
                        it.path
                    }, File.pathSeparator)
                )

                val kotlinArgs = arrayOf("-showWeaveInfo",
                    "-1.8",
                    "-inpath", project.buildDir.path + "/tmp/kotlin-classes/" + fullName,
                    "-aspectpath", javaCompile.classpath.asPath,
                    "-d", project.buildDir.path + "/tmp/kotlin-classes/" + fullName,
                    "-classpath", javaCompile.classpath.asPath,
                    "-bootclasspath", StringUtils.join(extension.bootClasspath.map {
                        it.path
                    }, File.pathSeparator))

                logger.warn("ajc args = ${args.contentToString()}")
                logger.warn("ajc kotlinArgs = ${kotlinArgs.contentToString()}")
                val handler = MessageHandler(false)
                Main().run(args, handler)
                Main().run(kotlinArgs, handler)
                for (message in handler.getMessages(null, true)) {
                    when (message.kind) {
                        IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {

                        }
                        IMessage.WARNING -> {

                        }
                        IMessage.INFO -> {

                        }
                        IMessage.DEBUG -> {

                        }
                    }
                }
            }
        }
    }

    override fun apply(project: Project) {
        val hasApp = project.plugins.hasPlugin(AppPlugin::class.java)
        val hasLibrary = project.plugins.hasPlugin(LibraryPlugin::class.java)
        if (!hasApp && !hasLibrary) {
            throw IllegalStateException("'android' or 'android-library' plugin required.")
        }
        project.dependencies.add("implementation", "org.aspectj:aspectjrt:1.9.5")
        val logger = project.logger
        if (hasApp) {
            val extension = project.extensions.getByType(AppExtension::class.java)
            extension.applicationVariants.all { variant ->
                doLastOnVariant(variant, extension, logger, project)
            }
        } else {
            val extension = project.extensions.getByType(LibraryExtension::class.java)
            extension.libraryVariants.all { variant ->
                doLastOnVariant(variant, extension, logger, project)
            }
        }
    }
}