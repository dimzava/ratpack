/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.codahale.metrics;

import com.google.inject.Inject;
import ratpack.codahale.metrics.internal.MetricsBroadcaster;
import ratpack.func.Action;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.path.PathBinder;
import ratpack.path.internal.PathHandler;
import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;

import static ratpack.websocket.WebSockets.websocket;

public class MetricsEndpoint extends PathHandler {

  @Inject
  public MetricsEndpoint(PathBinder binding, final MetricsBroadcaster broadcaster) {
    super(binding, new Handler() {

      @Override
      public void handle(final Context context) throws Exception {
        context.respond(context.getByMethod().
          get(new Runnable() {

            public void run() {
              websocket(context, new WebSocketHandler<Object>() {

                @Override
                public Object onOpen(final WebSocket webSocket) throws Exception {
                  return broadcaster.register(new Action<String>() {
                    @Override
                    public void execute(String thing) throws Exception {
                      webSocket.send(thing);
                    }
                  });
                }

                @Override
                public void onClose(WebSocketClose<Object> close) throws Exception {
                }

                @Override
                public void onMessage(WebSocketMessage<Object> frame) throws Exception {
                }

              });
            }

          })
        );
      }
    });
  }

}

