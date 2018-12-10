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

import java.util.concurrent.Executor;

import akka.util.ByteString;
import com.google.inject.Inject;
import play.Logger;
import play.libs.streams.Accumulator;
import play.mvc.*;

public class EssentialLoggingFilter extends EssentialFilter {

    private final Executor executor;

    @Inject
    public EssentialLoggingFilter(Executor executor) {
        super();
        this.executor = executor;
    }

    @Override
    public EssentialAction apply(EssentialAction next) {
        return EssentialAction.of(request -> {
            long startTime = System.currentTimeMillis();
            Accumulator<ByteString, Result> accumulator = next.apply(request);
            return accumulator.map(result -> {
                long endTime = System.currentTimeMillis();
                long requestTime = endTime - startTime;

                Logger.info("{} {} took {}ms and returned {}",
                        request.method(), request.uri(), requestTime, result.status());

                return result.withHeader("Request-Time", "" + requestTime);
            }, executor);
        });
    }
}
