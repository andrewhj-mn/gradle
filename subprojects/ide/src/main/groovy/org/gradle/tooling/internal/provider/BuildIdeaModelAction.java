/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.tooling.internal.provider;

import org.gradle.BuildResult;
import org.gradle.GradleLauncher;
import org.gradle.initialization.GradleLauncherAction;

public class BuildIdeaModelAction implements GradleLauncherAction {
    private ModelBuildingAdapter modelBuildingAdapter;

    public BuildIdeaModelAction(Class type) {
        modelBuildingAdapter = new ModelBuildingAdapter(new IdeaModelBuilder());
    }

    public BuildResult run(GradleLauncher launcher) {
        launcher.addListener(modelBuildingAdapter);
        return launcher.getBuildAnalysis();
    }

    public Object getResult() {
        return modelBuildingAdapter.getResult();
    }
}
