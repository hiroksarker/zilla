/*
 * Copyright 2021-2022 Aklivity Inc.
 *
 * Aklivity licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.aklivity.zilla.runtime.binding.kafka.internal.config;

import static io.aklivity.zilla.runtime.binding.kafka.internal.types.KafkaDeltaType.JSON_PATCH;
import static io.aklivity.zilla.runtime.binding.kafka.internal.types.KafkaOffsetType.LIVE;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.junit.Before;
import org.junit.Test;

public class KafkaOptionsConfigAdapterTest
{
    private Jsonb jsonb;

    @Before
    public void initJson()
    {
        JsonbConfig config = new JsonbConfig()
                .withAdapters(new KafkaOptionsConfigAdapter());
        jsonb = JsonbBuilder.create(config);
    }

    @Test
    public void shouldReadOptions()
    {
        String text =
                "{" +
                    "\"merged\":" +
                    "[" +
                        "\"test\"" +
                    "]," +
                    "\"bootstrap\":" +
                    "[" +
                        "\"test\"" +
                    "]," +
                    "\"topics\":" +
                    "[" +
                        "{" +
                            "\"name\": \"test\"," +
                            "\"defaultOffset\": \"live\"," +
                            "\"deltaType\": \"json_patch\"" +
                        "}" +
                    "]" +
                "}";

        KafkaOptionsConfig options = jsonb.fromJson(text, KafkaOptionsConfig.class);

        assertThat(options, not(nullValue()));
        assertThat(options.merged, equalTo(singletonList("test")));
        assertThat(options.bootstrap, equalTo(singletonList("test")));
        assertThat(options.topics, equalTo(singletonList(new KafkaTopicConfig("test", LIVE, JSON_PATCH))));
    }

    @Test
    public void shouldWriteOptions()
    {
        KafkaOptionsConfig options = new KafkaOptionsConfig(
                singletonList("test"),
                singletonList("test"),
                singletonList(new KafkaTopicConfig("test", LIVE, JSON_PATCH)));

        String text = jsonb.toJson(options);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"merged\":[\"test\"]," + "\"bootstrap\":[\"test\"]," +
                "\"topics\":[{\"name\":\"test\",\"defaultOffset\":\"live\",\"deltaType\":\"json_patch\"}]}"));
    }
}
