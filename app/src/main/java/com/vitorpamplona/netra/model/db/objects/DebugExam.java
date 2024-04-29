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
package com.vitorpamplona.netra.model.db.objects;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vitorpamplona.core.models.ComputedPrescription;
import com.vitorpamplona.domain.events.Event;
import com.vitorpamplona.netra.model.ExamResults;
import com.vitorpamplona.netra.model.Prescription;
import com.vitorpamplona.netra.model.RecommendedUseType;
import com.vitorpamplona.netra.model.RefractionType;
import com.vitorpamplona.netra.model.db.DataUtil;
import com.vitorpamplona.netra.model.db.gsonadapters.ComputedPrescriptionJsonInterfaceAdapter;
import com.vitorpamplona.netra.model.db.gsonadapters.TypedJsonInterfaceAdapter;
import com.vitorpamplona.netra.model.db.tables.DebugExamTable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DebugExam extends SQLiteModel {

    protected Date mTested;

    protected String mStatus;
    protected String mStudyName;
    protected Integer mSequenceNumber;
    protected String mEnvironment;
    protected String mShareWith;
    protected String mUserToken;
    protected String mUserName;

    protected Integer mSyncIdPrescribed;

    protected Float mLatitude;
    protected Float mLongitude;

    protected RefractionType mDistancePreference;
    protected RefractionType mNearPreference;

    protected Date mDateOfBirth;

    protected float mFittingQualityLeft;
    protected float mFittingQualityRight;

    protected long deviceId;
    protected String appVersion;

    protected Date prescriptionExpiration;
    protected String prescriptionEmail;
    protected String prescriptionPhone;
    protected List<RecommendedUseType> prescriptionRecommendedUse;


    protected Map<RefractionType, Refraction> mRefractions = new HashMap<RefractionType, Refraction>();

    public DebugExam() {

    }

    public DebugExam(Cursor c) {
        updateFromCursor(c);
    }

    public DebugExam(JSONObject json) {
        updateFromJson(json);
    }

    public DebugExam(ExamResults r) {
        updateFromExamResults(r);
    }

    public String getStatus() {
        if (null == mStatus) {
            mStatus = "ok";
        }
        return mStatus;
    }

    public void setStatus(String s) {
        if (null == s) {
            s = "ok";
        }
        mStatus = s;
    }

    public Date getTested() {
        return mTested;
    }

    public void setTested(Date d) {
        mTested = d;
    }

    public String getStudyName() {
        if (mStudyName == null) {
            mStudyName = "";
        }
        return mStudyName;
    }

    public void setStudyName(String s) {
        mStudyName = s;
    }

    public Integer getSequenceNumber() {
        return mSequenceNumber;
    }

    public void setSequenceNumber(Integer i) {
        mSequenceNumber = i;
    }

    public String getEnvironment() {
        return mEnvironment;
    }

    public void setEnvironment(String e) {
        mEnvironment = e;
    }

    public String getShareWith() {
        return mShareWith;
    }

    public void setShareWith(String s) {
        mShareWith = s;
    }

    public String getUserToken() {
        return mUserToken;
    }

    public void setUserToken(String s) {
        mUserToken = s;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String s) {
        mUserName = s;
    }

    public Float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Float f) {
        mLatitude = f;
    }

    public Float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Float f) {
        mLongitude = f;
    }

    public Integer getPrescriptionSyncId() {
        return mSyncIdPrescribed;
    }

    public void setPrescriptionSyncId(Integer syncId) {
        mSyncIdPrescribed = syncId;
    }

    public RefractionType getDistancePreference() {
        return mDistancePreference;
    }

    public void setDistancePreference(RefractionType r) {
        mDistancePreference = r;
    }

    public RefractionType getNearPreference() {
        return mNearPreference;
    }

    public void setNearPreference(RefractionType r) {
        mNearPreference = r;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date d) {
        mDateOfBirth = d;
    }

    public Date getPrescriptionExpiration() {
        return prescriptionExpiration;
    }

    public List<RecommendedUseType> getPrescriptionRecommendedUse() {
        if (prescriptionRecommendedUse == null) {
            prescriptionRecommendedUse = new ArrayList<RecommendedUseType>();
        }

        return prescriptionRecommendedUse;
    }

    public void setPrescriptionRecommendedUse(List<RecommendedUseType> prescriptionRecommendedUse) {
        this.prescriptionRecommendedUse = prescriptionRecommendedUse;
    }

    public void setPrescriptionExpiration(Date prescriptionExpiration) {
        this.prescriptionExpiration = prescriptionExpiration;
    }

    public String getPrescriptionEmail() {
        return prescriptionEmail;
    }

    public void setPrescriptionEmail(String prescriptionEmail) {
        this.prescriptionEmail = prescriptionEmail;
    }

    public String getPrescriptionPhone() {
        return prescriptionPhone;
    }

    public void setPrescriptionPhone(String prescriptionPhone) {
        this.prescriptionPhone = prescriptionPhone;
    }


    public Map<RefractionType, Refraction> getRefractions() {
        if (mRefractions == null) {
            mRefractions = new HashMap<RefractionType, Refraction>();
        }
        return mRefractions;
    }

    public void setRefractions(Map<RefractionType, Refraction> rm) {
        mRefractions = rm;
    }

    public Refraction getRefraction(RefractionType t) {
        return mRefractions.get(t);
    }

    public void setRefraction(RefractionType rt, Refraction r) {
        mRefractions.put(rt, r);
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void updateFromExamResults(ExamResults e) {
        //TODO: async the original_data/history conversion?

        setTested(e.getExamDate());

        setStatus("ok");
        setStudyName(e.getStudyName());
        setSequenceNumber(e.getSequenceNumber());
        setEnvironment(e.getEnvironment());
        setShareWith(e.getShareWith());
        setUserToken(e.getUserToken());
        setUserName(e.getUserName());
        setDeviceId(e.getDevice().id);
        setAppVersion(e.getAppVersion());
        setSyncId(e.getId());

        setLatitude(Float.valueOf((float) e.getLatitude()));
        setLongitude(Float.valueOf((float) e.getLongitude()));

        for (RefractionType t : RefractionType.values()) {
            Prescription right = e.getRightEye().get(t);
            Prescription left = e.getLeftEye().get(t);

            if (right == null || left == null) continue;

            addRefraction(e, t);

            if (t == RefractionType.NETRA) {
                setFittingQualityLeft(left.getFittingQuality());
                setFittingQualityRight(right.getFittingQuality());
            }
        }
    }

    public void addRefraction(ExamResults e, RefractionType type) {
        Prescription right = e.getRightEye().get(type);
        Prescription left = e.getLeftEye().get(type);

        if (right == null || left == null) return;

        GsonBuilder gsonb = new GsonBuilder()
                .registerTypeAdapter(Event.class, new TypedJsonInterfaceAdapter<Event>())
                .registerTypeAdapter(ComputedPrescription.class, new ComputedPrescriptionJsonInterfaceAdapter());
        Gson gson = gsonb.create();

        Refraction r = new Refraction();
        r.setDebugExam(this);
        r.setRefractionType(right.getProcedure());

        r.setRightPd(right.getNosePupilDistance());
        r.setRightSphere(right.getSphere());
        r.setRightCylinder(right.getCylinder());
        r.setRightAxis(right.getAxis());
        r.setRightAdd(right.getAddLens());

        if (right.getOriginalData() != null && !right.getOriginalData().testResults().isEmpty()) {
            r.setRightOriginalData(gson.toJson(right.getOriginalData()));
        }
        if (right.history() != null && !right.history().eventHistory().isEmpty()) {
            r.setRightHistory(gson.toJson(right.history()));
        }

        r.setLeftPd(left.getNosePupilDistance());
        r.setLeftSphere(left.getSphere());
        r.setLeftCylinder(left.getCylinder());
        r.setLeftAxis(left.getAxis());
        r.setLeftAdd(left.getAddLens());

        if (left.getOriginalData() != null && !left.getOriginalData().testResults().isEmpty()) {
            r.setLeftOriginalData(gson.toJson(left.getOriginalData()));
        }
        if (left.history() != null && !left.history().eventHistory().isEmpty()) {
            r.setLeftHistory(gson.toJson(left.history()));
        }

        setRefraction(r.getRefractionType(), r);
    }

    @Override
    public void updateFromCursor(Cursor c) {
        super.updateFromCursor(c);

        setTested(DataUtil.timestampStringToDate(DataUtil.getString(c, DebugExamTable.TESTED)));
        setDeviceId(DataUtil.getLong(c, DebugExamTable.DEVICE_ID));
        setAppVersion(DataUtil.getString(c, DebugExamTable.APP_VERSION));
        setStatus(DataUtil.getString(c, DebugExamTable.STATUS));

        setStudyName(DataUtil.getString(c, DebugExamTable.STUDY_NAME));
        setSequenceNumber(DataUtil.getInteger(c, DebugExamTable.SEQUENCE_NUMBER));
        setEnvironment(DataUtil.getString(c, DebugExamTable.ENVIRONMENT));
        setShareWith(DataUtil.getString(c, DebugExamTable.SHARE_WITH));
        setUserToken(DataUtil.getString(c, DebugExamTable.SERVER_USER_TOKEN));
        setUserName(DataUtil.getString(c, DebugExamTable.SERVER_USER_NAME));

        setLatitude(DataUtil.getFloat(c, DebugExamTable.LATITUDE));
        setLongitude(DataUtil.getFloat(c, DebugExamTable.LONGITUDE));

        setPrescriptionSyncId(DataUtil.getInteger(c, DebugExamTable.PRESCRIPTION_SYNC_ID));

        setDistancePreference(DataUtil.stringToEnum(RefractionType.class, DataUtil.getString(c, DebugExamTable.DISTANCE_PREFERENCE)));
        setNearPreference(DataUtil.stringToEnum(RefractionType.class, DataUtil.getString(c, DebugExamTable.NEAR_PREFERENCE)));

        setDateOfBirth(DataUtil.dateStringToDate(DataUtil.getString(c, DebugExamTable.DATE_OF_BIRTH)));
        setFittingQualityLeft(DataUtil.getFloat(c, DebugExamTable.FITTING_QUALITY_LEFT));
        setFittingQualityRight(DataUtil.getFloat(c, DebugExamTable.FITTING_QUALITY_RIGHT));

        setPrescriptionExpiration(DataUtil.dateStringToDate(DataUtil.getString(c, DebugExamTable.PRESCRIPTION_EXPIRATION_DATE)));
        setPrescriptionEmail(DataUtil.getString(c, DebugExamTable.PRESCRIPTION_EMAIL));
        setPrescriptionPhone(DataUtil.getString(c, DebugExamTable.PRESCRIPTION_PHONE));
        setPrescriptionRecommendedUse(DataUtil.jsonStringToEnumList(RecommendedUseType.class, DataUtil.getString(c, DebugExamTable.PRESCRIPTION_RECOMMENDED_USE)));
    }

    @Override
    public void updateFromJson(JSONObject json) {
        //probably not ever used
        if (json == null) {
            return;
        }
        super.updateFromJson(json);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        //PostEyeTest used instead of this for now

        return json;
    }

    public float getFittingQualityLeft() {
        return mFittingQualityLeft;
    }

    public void setFittingQualityLeft(Float mFittingQuality) {
        if (mFittingQuality == null) {
            this.mFittingQualityLeft = 0;
        } else {
            this.mFittingQualityLeft = mFittingQuality;
        }
    }

    public float getFittingQualityRight() {
        return mFittingQualityRight;
    }

    public void setFittingQualityRight(Float mFittingQuality) {
        if (mFittingQuality == null) {
            this.mFittingQualityRight = 0;
        } else {
            this.mFittingQualityRight = mFittingQuality;
        }
    }

    public boolean isPrescribed() {
        return mSyncIdPrescribed != null && mSyncIdPrescribed > 0;
    }

    public boolean hasCompleteRefractiveData() {
        Refraction netra = getRefraction(RefractionType.NETRA);
        Refraction subj = getRefraction(RefractionType.SUBJECTIVE);

        boolean missingData = false;

        if (subj != null) {
            if (subj.getRightSphere() == null || subj.getLeftSphere() == null || subj.getPd() == null) {
                missingData = true;
            }
        } else if (netra != null) {
            if (netra.getRightSphere() == null || netra.getLeftSphere() == null || netra.getPd() == null) {
                missingData = true;
            }
        } else {
            missingData = true;
        }

        return !missingData;
    }

    public boolean isReadyToPrescribe() {
        return (prescriptionEmail != null && !prescriptionEmail.trim().isEmpty()
                || prescriptionPhone != null && !prescriptionPhone.trim().isEmpty())
                && getStudyName() != null && !getStudyName().trim().isEmpty()
                && getDateOfBirth() != null
                && hasCompleteRefractiveData()
                ;
    }

    public Refraction getOrCreateFrom(RefractionType subjective, RefractionType netra) {
        Refraction p = getRefraction(subjective);
        if (p != null)
            return p;

        Refraction copyFrom = getRefraction(netra);
        Refraction newNetra = new Refraction();
        newNetra.setRefractionType(subjective);
        if (copyFrom != null) {
            newNetra.setLeftSphere(copyFrom.getLeftSphere());
            newNetra.setLeftCylinder(copyFrom.getLeftCylinder());
            newNetra.setLeftAxis(copyFrom.getLeftAxis());
            newNetra.setLeftPd(copyFrom.getLeftPd());
            newNetra.setLeftAdd(copyFrom.getLeftAdd());
            newNetra.setRightSphere(copyFrom.getRightSphere());
            newNetra.setRightCylinder(copyFrom.getRightCylinder());
            newNetra.setRightAxis(copyFrom.getRightAxis());
            newNetra.setRightPd(copyFrom.getRightPd());
            newNetra.setRightAdd(copyFrom.getRightAdd());
        }
        newNetra.setDebugExam(this);
        setRefraction(subjective, newNetra);

        return newNetra;
    }
}
