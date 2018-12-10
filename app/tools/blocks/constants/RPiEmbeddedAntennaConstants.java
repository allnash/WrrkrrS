// Copyright 2018 OmegaTrace Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License

package tools.blocks.constants;

public class RPiEmbeddedAntennaConstants implements MeasurementDeviceConstants {

    private double coefficientA;
    private double coefficientB;
    private double coefficientC;

    public RPiEmbeddedAntennaConstants() {
        coefficientA = 0.42093;
        coefficientB = 6.9476;
        coefficientC = 0.54992;
    }

    @Override
    public double getCoefficientA() {
        return coefficientA;
    }

    @Override
    public double getCoefficientB() {
        return coefficientB;
    }

    @Override
    public double getCoefficientC() {
        return coefficientC;
    }
}
