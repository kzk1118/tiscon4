package jp.co.tis.tiscon4.form;

import jp.co.tis.tiscon4.common.code.JobType;
import jp.co.tis.tiscon4.common.code.TreatedType;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

import javax.validation.constraints.AssertTrue;
import java.io.Serializable;

public class UserForm2 implements Serializable {

    private static final long serialVersionUID = 1L;



    /** 配偶者有無 */
    @Required
    @Domain("married")
    private String married;

    /** 所得金額 */
    @Required
    @Domain("income")
    private String income;

    /** 職業 */
    @Required
    @Domain("job")
    private String job;

    /** その他の職業 */
    @Domain("otherJob")
    private String otherJob;

    /** 治療歴有無 */
    @Required
    @Domain("treated")
    private String treated;

    /** 既往歴 */
    @Domain("medicalHistory")
    private String medicalHistory;


    public String getMarried() {
        return married;
    }

    public void setMarried(String married) {
        this.married = married;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getOtherJob() {
        return otherJob;
    }

    public void setOtherJob(String otherJob) {
        this.otherJob = otherJob;
    }

    public String getTreated() {
        return treated;
    }

    public void setTreated(String treated) {
        this.treated = treated;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    /**
     * その他の職業欄が正しく入力されているかどうか判定する。
     * 職業で「その他（有職）」を選択し、その他の職業欄に何らかの値が入力された場合、正しく入力されたと判定する。
     *
     * @return その他の職業欄に入力がある場合にtrue
     */
    @AssertTrue(message = "{tiscon4.order.inputUser.error.hasValueOtherJob}")
    public boolean hasValueOtherJob() {
        if (StringUtil.isNullOrEmpty(job)) {
            // 職業が未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
            return true;
        } else if (JobType.EMPLOYED.getCode().equals(job) && StringUtil.isNullOrEmpty(otherJob)) {
            return false;
        }
        return true;
    }

    /**
     * 既往歴が正しく入力されているかどうか判定する。
     * 治療有無で「はい」を選択し、既往歴に何らかの値が入力された場合、正しく入力されたと判定する。
     *
     * @return 既往歴に入力がある場合にtrue
     */
    @AssertTrue(message = "{tiscon4.order.inputUser.error.hasValueMedicalHistory}")
    public boolean hasValueMedicalHistory() {
        if (StringUtil.isNullOrEmpty(treated)) {
            // 治療有無が未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
            return true;
        } else if (TreatedType.TREATED.getCode().equals(treated) && StringUtil.isNullOrEmpty(medicalHistory)) {
            return false;
        }
        return true;
    }

}
