package com.guang.android.point

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPlugin implements Plugin<Project>{

    @Override
    void apply(Project target) {
        def android = target.extensions.findByType(AppExtension)
        android.registerTransform(new MyTransform(target))
    }
}