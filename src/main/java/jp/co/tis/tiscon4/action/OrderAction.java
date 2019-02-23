package jp.co.tis.tiscon4.action;

import jp.co.tis.tiscon4.common.code.GenderType;
import jp.co.tis.tiscon4.common.code.IndustryType;
import jp.co.tis.tiscon4.common.code.JobType;
import jp.co.tis.tiscon4.common.code.MarriedType;
import jp.co.tis.tiscon4.common.code.TreatedType;
import jp.co.tis.tiscon4.dto.ZipcodeDto;
import jp.co.tis.tiscon4.entity.InsuranceOrder;
import jp.co.tis.tiscon4.form.*;
import nablarch.common.dao.UniversalDao;
import nablarch.common.web.interceptor.InjectForm;
import nablarch.common.web.session.SessionUtil;
import nablarch.common.web.token.OnDoubleSubmission;
import nablarch.common.web.token.UseToken;
import nablarch.core.beans.BeanUtil;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.validation.ValidationUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

/**
 * 保険申し込みに関するActionクラス.
 *
 * @author Kudo Sae
 */
public class OrderAction {

    /**
     * 加入条件確認画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = IndexForm.class)
    @OnError(type = ApplicationException.class, path = "index.html")
    public HttpResponse accept(HttpRequest req, ExecutionContext ctx) {
        IndexForm indexForm = ctx.getRequestScopedVar("form");
        InsuranceOrder insOrder = BeanUtil.createAndCopy(InsuranceOrder.class, indexForm);

        SessionUtil.delete(ctx, "insOrder");
        SessionUtil.put(ctx, "insOrder", insOrder);

        ctx.setRequestScopedVar("form", new AcceptForm());

        return new HttpResponse("acceptance.html");
    }

    /**
     * 本人登録画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = AcceptForm.class)
    @OnError(type = ApplicationException.class, path = "acceptance.html")
    public HttpResponse inputUser(HttpRequest req, ExecutionContext ctx) {
        ctx.setRequestScopedVar("form", new UserForm());
        ctx.setRequestScopedVar("genderTypes", GenderType.values());
      /*  ctx.setRequestScopedVar("marriedTypes", MarriedType.values());
        ctx.setRequestScopedVar("jobTypes", JobType.values());
        ctx.setRequestScopedVar("treatedTypes", TreatedType.values()); */

        return new HttpResponse("user.html");
    }

    /**
     * 本人登録画面２を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = UserForm.class)
    @OnError(type = ApplicationException.class, path = "forward://inputUserForError")
    public HttpResponse inputUser2(HttpRequest req, ExecutionContext ctx) {
        UserForm form = ctx.getRequestScopedVar("form");
        InsuranceOrder insOrder = SessionUtil.get(ctx, "insOrder");

        ctx.setRequestScopedVar("form", new UserForm2());
      //  ctx.setRequestScopedVar("genderTypes", GenderType.values());
        ctx.setRequestScopedVar("marriedTypes", MarriedType.values());
        ctx.setRequestScopedVar("jobTypes", JobType.values());
        ctx.setRequestScopedVar("treatedTypes", TreatedType.values());

        BeanUtil.copy(form, insOrder);

        return new HttpResponse("user2.html");
    }

    /**
     * お勤め先登録画面を表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = UserForm2.class)
    @OnError(type = ApplicationException.class, path = "forward://inputUserForError2")
    @UseToken
    public HttpResponse inputJob(HttpRequest req, ExecutionContext ctx) {
        UserForm2 form = ctx.getRequestScopedVar("form");
        InsuranceOrder insOrder = SessionUtil.get(ctx, "insOrder");

        // treatLadyは女性しか加入できないため、性別選択チェックを行う。
        if (insOrder.getInsuranceType().equals("treatLady") && insOrder.getGender().equals("male")) {
            Message message = ValidationUtil.createMessageForProperty("gender", "tiscon4.order.inputUser.error.gender");
            throw new ApplicationException(message);
        }

        UniversalDao.findAllBySqlFile(ZipcodeDto.class, "ZIPCODE_LIST");

        BeanUtil.copy(form, insOrder);

        ctx.setRequestScopedVar("form", new JobForm());
        ctx.setRequestScopedVar("industryTypes", IndustryType.values());

        return new HttpResponse("job.html");
    }

    /**
     * 本人登録画面に入力エラーがあった時に再表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse inputUserForError(HttpRequest req, ExecutionContext ctx) {
        ctx.setRequestScopedVar("genderTypes", GenderType.values());
       /* ctx.setRequestScopedVar("marriedTypes", MarriedType.values());
        ctx.setRequestScopedVar("jobTypes", JobType.values());
        ctx.setRequestScopedVar("treatedTypes", TreatedType.values());*/

        return new HttpResponse("user.html");
    }

    /**
     * 本人登録画面2に入力エラーがあった時に再表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse inputUserForError2(HttpRequest req, ExecutionContext ctx) {
        //ctx.setRequestScopedVar("genderTypes", GenderType.values());
        ctx.setRequestScopedVar("marriedTypes", MarriedType.values());
        ctx.setRequestScopedVar("jobTypes", JobType.values());
        ctx.setRequestScopedVar("treatedTypes", TreatedType.values());

        return new HttpResponse("user2.html");
    }

    /**
     * 申し込み情報をデータベースに登録する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @InjectForm(form = JobForm.class)
    @OnError(type = ApplicationException.class, path = "forward://inputJobForError")
    @OnDoubleSubmission(path = "doubleSubmissionError.html")
    public HttpResponse create(HttpRequest req, ExecutionContext ctx) {
        JobForm form = ctx.getRequestScopedVar("form");
        InsuranceOrder insOrder = SessionUtil.get(ctx, "insOrder");

        BeanUtil.copy(form, insOrder);

        UniversalDao.insert(insOrder);

        return new HttpResponse("redirect://completed");
    }

    /**
     * お勤め先登録画面に入力エラーがあった時に再表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    @UseToken
    public HttpResponse inputJobForError(HttpRequest req, ExecutionContext ctx) {
        ctx.setRequestScopedVar("industryTypes", IndustryType.values());

        return new HttpResponse("job.html");
    }

    /**
     * 完了ページを表示する。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse completed(HttpRequest req, ExecutionContext ctx) {
        return new HttpResponse("completed.html");
    }

    /**
     * 加入条件確認画面に戻る。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse backToAccept(HttpRequest req, ExecutionContext ctx) {
        InsuranceOrder oldInsOrder = SessionUtil.delete(ctx, "insOrder");
        InsuranceOrder newInsOrder = new InsuranceOrder();

        // 本人情報は改めて入力するため、保険の区分のみ情報を引き継ぐ。
        newInsOrder.setInsuranceType(oldInsOrder.getInsuranceType());
        SessionUtil.put(ctx, "insOrder", newInsOrder);

        ctx.setRequestScopedVar("form", new AcceptForm());

        return new HttpResponse("acceptance.html");
    }

    /**
     * 本人登録画面に戻る。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse backToUser(HttpRequest req, ExecutionContext ctx) {
        InsuranceOrder insOrder = SessionUtil.get(ctx, "insOrder");
        UserForm form = new UserForm();
        BeanUtil.copy(insOrder, form);

        ctx.setRequestScopedVar("form", form);
        ctx.setRequestScopedVar("genderTypes", GenderType.values());
        /*ctx.setRequestScopedVar("marriedTypes", MarriedType.values());
        ctx.setRequestScopedVar("jobTypes", JobType.values());
        ctx.setRequestScopedVar("treatedTypes", TreatedType.values());*/

        return new HttpResponse("user.html");
    }

    /**
     * 本人登録画面2に戻る。
     *
     * @param req リクエストコンテキスト
     * @param ctx HTTPリクエストの処理に関連するサーバ側の情報
     * @return HTTPレスポンス
     */
    public HttpResponse backToUser2(HttpRequest req, ExecutionContext ctx) {
        InsuranceOrder insOrder = SessionUtil.get(ctx, "insOrder");
        UserForm2 form = new UserForm2();
        BeanUtil.copy(insOrder, form);

        ctx.setRequestScopedVar("form", form);
       // ctx.setRequestScopedVar("genderTypes", GenderType.values());
        ctx.setRequestScopedVar("marriedTypes", MarriedType.values());
        ctx.setRequestScopedVar("jobTypes", JobType.values());
        ctx.setRequestScopedVar("treatedTypes", TreatedType.values());

        return new HttpResponse("user2.html");
    }

}
