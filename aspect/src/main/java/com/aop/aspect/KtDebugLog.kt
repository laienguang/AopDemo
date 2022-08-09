package com.aop.aspect

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR
)
@Retention(
    RetentionPolicy.CLASS
)
annotation class KtDebugLog