/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.performance.fixture

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.gradle.performance.results.BuildDisplayInfo
import org.gradle.profiler.BuildMutator
import org.gradle.profiler.InvocationSettings

import java.util.function.Function

@CompileStatic
@EqualsAndHashCode
class MavenBuildExperimentSpec extends BuildExperimentSpec {
    final MavenInvocationSpec invocation

    MavenBuildExperimentSpec(String displayName,
                             String projectName,
                             File workingDirectory,
                             MavenInvocationSpec mavenInvocation,
                             Integer warmUpCount,
                             Integer invocationCount,
                             InvocationCustomizer invocationCustomizer,
                             ImmutableList<Function<InvocationSettings, BuildMutator>> buildMutators
    ) {
        super(displayName, projectName, workingDirectory, warmUpCount, invocationCount, invocationCustomizer, buildMutators)
        this.invocation = mavenInvocation
    }

    static MavenBuilder builder() {
        new MavenBuilder()
    }

    @Override
    BuildDisplayInfo getDisplayInfo() {
        new BuildDisplayInfo(projectName, displayName, invocation.tasksToRun, invocation.cleanTasks, invocation.args, invocation.mavenOpts, false)
    }

    static class MavenBuilder implements BuildExperimentSpec.Builder {
        String displayName
        String projectName
        File workingDirectory
        MavenInvocationSpec.InvocationBuilder invocation = MavenInvocationSpec.builder()

        Integer warmUpCount
        Integer invocationCount
        InvocationCustomizer invocationCustomizer
        final List<Function<InvocationSettings, BuildMutator>> buildMutators = []

        MavenBuilder invocation(@DelegatesTo(MavenInvocationSpec.InvocationBuilder) Closure<?> conf) {
            invocation.with(conf)
            this
        }

        MavenBuilder displayName(String displayName) {
            this.displayName = displayName
            this
        }

        MavenBuilder projectName(String projectName) {
            this.projectName = projectName
            this
        }

        MavenBuilder warmUpCount(Integer warmUpCount) {
            this.warmUpCount = warmUpCount
            this
        }

        MavenBuilder invocationCount(Integer invocationCount) {
            this.invocationCount = invocationCount
            this
        }

        MavenBuilder invocationCustomizer(InvocationCustomizer invocationCustomizer) {
            this.invocationCustomizer = invocationCustomizer
            this
        }

        MavenBuilder addBuildMutator(Function<InvocationSettings, BuildMutator> buildMutator) {
            this.buildMutators.add(buildMutator)
            this
        }

        BuildExperimentSpec build() {
            assert projectName != null
            assert displayName != null
            assert invocation != null

            new MavenBuildExperimentSpec(
                displayName,
                projectName,
                workingDirectory,
                invocation.build(),
                warmUpCount,
                invocationCount,
                invocationCustomizer,
                ImmutableList.copyOf(buildMutators)
            )
        }

    }
}
