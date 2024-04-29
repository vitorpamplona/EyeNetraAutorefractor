/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.netra.model.db.gsonadapters;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.StringMap;
import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.ComputedPrescription;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.utils.FloatHashMap;
import com.vitorpamplona.core.utils.HistoryHashMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComputedPrescriptionJsonInterfaceAdapter implements JsonSerializer<ComputedPrescription>, JsonDeserializer<ComputedPrescription> {

    public JsonElement serialize(ComputedPrescription object, Type interfaceType, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        Gson builder = new Gson();
        wrapper.add("accepted", builder.toJsonTree(object.getAccepted(), object.getAccepted().getClass()));
        wrapper.add("rounded", builder.toJsonTree(object.getRounded(), object.getRounded().getClass()));
        wrapper.add("fitted", builder.toJsonTree(object.getFitted(), object.getFitted().getClass()));
        wrapper.add("data", builder.toJsonTree(object.testResults().getData().getData(), object.testResults().getData().getData().getClass()));
        wrapper.add("history", builder.toJsonTree(object.testResults().getData().getHistory(), object.testResults().getData().getHistory().getClass()));
        wrapper.add("fails", builder.toJsonTree(object.getFails()));
        return wrapper;
    }

    public MeridianPower toMeridianPower(StringMap<?> label) {
        MeridianPower newPower = new MeridianPower();
        newPower.setAngle(((Double) label.get("angle")).floatValue());
        newPower.setPower(((Double) label.get("power")).floatValue());
        newPower.setOutlier((Boolean) label.get("outlier"));
        return newPower;
    }

    public HashMap<Integer, MeridianPower> toMeridianPowers(LinkedHashMap<?, ?> recoveredData) {
        HashMap<Integer, MeridianPower> convertedData = new HashMap<Integer, MeridianPower>();
        for (Object angle : recoveredData.keySet()) {
            StringMap<?> label = (StringMap<?>) recoveredData.get(angle);
            convertedData.put(Integer.parseInt((String) angle), toMeridianPower(label));
        }
        return convertedData;
    }

    public List<MeridianPower> toMeridianPowers(List<StringMap<?>> recoveredData) {
        List<MeridianPower> convertedPowers = new ArrayList<MeridianPower>();
        for (StringMap<?> label : recoveredData) {
            convertedPowers.add(toMeridianPower(label));
        }
        return convertedPowers;
    }

    public ComputedPrescription deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement accepted = wrapper.get("accepted");
        final JsonElement rounded = wrapper.get("rounded");
        final JsonElement fitted = wrapper.get("fitted");
        final JsonElement data = wrapper.get("data");
        final JsonElement history = wrapper.get("history");
        JsonElement failsElem = wrapper.get("fails");
        Integer fails = 0;
        if (failsElem != null)
            fails = failsElem.getAsInt();

        LinkedHashMap<?, ?> recoveredData = context.deserialize(data, Map.class);
        HashMap<Integer, MeridianPower> convertedData = toMeridianPowers(recoveredData);

        LinkedHashMap<?, ?> recoveredHistory = context.deserialize(history, Map.class);
        HashMap<Integer, List<MeridianPower>> convertedHistory = new HashMap<Integer, List<MeridianPower>>();

        for (Object angle : recoveredHistory.keySet()) {
            List<StringMap<?>> powers = (List<StringMap<?>>) recoveredHistory.get(angle);
            convertedHistory.put(Integer.parseInt((String) angle), toMeridianPowers(powers));
        }

        AstigmaticLensParams recoveredAccepted = context.deserialize(accepted, AstigmaticLensParams.class);
        AstigmaticLensParams recoveredRounded = context.deserialize(rounded, AstigmaticLensParams.class);
        AstigmaticLensParams recoveredFitted = context.deserialize(fitted, AstigmaticLensParams.class);

        HistoryHashMap<Integer, MeridianPower> map = new HistoryHashMap<Integer, MeridianPower>();
        map.setData(convertedData);
        map.setHistory(convertedHistory);

        FloatHashMap<MeridianPower> finalMap = new FloatHashMap<MeridianPower>();
        finalMap.setData(map);

        ComputedPrescription ret = new ComputedPrescription();
        ret.setAccepted(recoveredAccepted);
        ret.setRounded(recoveredRounded);
        ret.setFitted(recoveredFitted);
        ret.setTestResults(finalMap);
        ret.setFails(fails);
        return ret;
    }

}