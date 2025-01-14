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

import java.util.ArrayList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.bind.adapter.JsonbAdapter;

import io.aklivity.zilla.runtime.binding.kafka.internal.KafkaBinding;
import io.aklivity.zilla.runtime.engine.config.OptionsConfig;
import io.aklivity.zilla.runtime.engine.config.OptionsConfigAdapterSpi;
import io.aklivity.zilla.runtime.engine.config.OptionsConfigAdapterSpi.Kind;

public final class KafkaOptionsConfigAdapter implements OptionsConfigAdapterSpi, JsonbAdapter<OptionsConfig, JsonObject>
{
    private static final String MERGED_NAME = "merged";
    private static final String BOOTSTRAP_NAME = "bootstrap";
    private static final String TOPICS_NAME = "topics";

    private final KafkaTopicConfigAdapter topic = new KafkaTopicConfigAdapter();

    @Override
    public Kind kind()
    {
        return Kind.BINDING;
    }

    @Override
    public String type()
    {
        return KafkaBinding.NAME;
    }

    @Override
    public JsonObject adaptToJson(
        OptionsConfig options)
    {
        KafkaOptionsConfig kafkaOptions = (KafkaOptionsConfig) options;

        JsonObjectBuilder object = Json.createObjectBuilder();

        if (kafkaOptions.merged != null &&
            !kafkaOptions.merged.isEmpty())
        {
            JsonArrayBuilder entries = Json.createArrayBuilder();
            kafkaOptions.merged.forEach(m -> entries.add(m));

            object.add(MERGED_NAME, entries);
        }

        if (kafkaOptions.bootstrap != null &&
            !kafkaOptions.bootstrap.isEmpty())
        {
            JsonArrayBuilder entries = Json.createArrayBuilder();
            kafkaOptions.bootstrap.forEach(b -> entries.add(b));

            object.add(BOOTSTRAP_NAME, entries);
        }


        if (kafkaOptions.topics != null &&
            !kafkaOptions.topics.isEmpty())
        {
            JsonArrayBuilder entries = Json.createArrayBuilder();
            kafkaOptions.topics.forEach(t -> entries.add(topic.adaptToJson(t)));

            object.add(TOPICS_NAME, entries);
        }

        return object.build();
    }

    @Override
    public OptionsConfig adaptFromJson(
        JsonObject object)
    {
        JsonArray mergedArray = object.containsKey(MERGED_NAME)
                ? object.getJsonArray(MERGED_NAME)
                : null;

        JsonArray bootstrapArray = object.containsKey(BOOTSTRAP_NAME)
                ? object.getJsonArray(BOOTSTRAP_NAME)
                : null;

        JsonArray topicsArray = object.containsKey(TOPICS_NAME)
                ? object.getJsonArray(TOPICS_NAME)
                : null;

        List<String> merged = null;

        if (mergedArray != null)
        {
            List<String> merged0 = new ArrayList<>();
            mergedArray.forEach(v -> merged0.add(JsonString.class.cast(v).getString()));
            merged = merged0;
        }

        List<String> bootstrap = null;

        if (bootstrapArray != null)
        {
            List<String> bootstrap0 = new ArrayList<>();
            bootstrapArray.forEach(v -> bootstrap0.add(JsonString.class.cast(v).getString()));
            bootstrap = bootstrap0;
        }

        List<KafkaTopicConfig> topics = null;

        if (topicsArray != null)
        {
            List<KafkaTopicConfig> topics0 = new ArrayList<>();
            topicsArray.forEach(v -> topics0.add(topic.adaptFromJson(v.asJsonObject())));
            topics = topics0;
        }

        return new KafkaOptionsConfig(merged, bootstrap, topics);
    }
}
