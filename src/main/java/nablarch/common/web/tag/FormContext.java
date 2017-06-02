package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.util.StringUtil;


/**
 * フォームのコンテキスト情報を保持するクラス。<br>
 * このコンテキスト情報はページコンテキストに格納する。
 * @author Kiyohito Itoh
 */
public class FormContext {
    
    /** フォームのname属性 */
    private final String name;
    
    /** フォームに含まれる入力項目のname属性 */
    private final Set<String> inputNames = new HashSet<String>();
    
    /** フォームに含まれるサブミット情報 */
    private final List<SubmissionInfo> submissionInfoList = new ArrayList<SubmissionInfo>();
    
    /** フォームに含まれるサブミット情報のname属性 */
    private final Set<String> submissionInfoNames = new HashSet<String>();
    
    /** フォームに含まれるリクエストID */
    private final List<String> requestIds = new ArrayList<String>();
    
    /** フォームに含まれるhiddenタグの情報 */
    private final List<HtmlAttributes> hiddenTagInfoList = new ArrayList<HtmlAttributes>();
    
    /** カレントのサブミット情報 */
    private SubmissionInfo currentSubmissionInfo;
    
    /**
     * コンストラクタ。
     * @param name フォームのname属性
     */
    public FormContext(String name) {
        this.name = name;
    }
    
    /**
     * フォームのname属性を取得する。
     * @return フォームのname属性
     */
    public String getName() {
        return name;
    }
    
    /**
     * フォームに含まれる入力項目のname属性を追加する。
     * @param name 入力項目のname属性
     */
    public void addInputName(String name) {
        inputNames.add(name);
    }
    
    /**
     * フォームに含まれる入力項目のname属性を取得する。
     * @return フォームに含まれる入力項目のname属性
     */
    public Set<String> getInputNames() {
        return inputNames;
    }
    
    /**
     * フォームに含まれるサブミット情報を追加する。
     * <p/>
     * サブミットタグのname属性が指定されていない場合、もしくは
     * 同一FORM内で重複している場合は、FORM内で一意となるname属性値を発番し
     * 返却する。
     * 
     * @param action サブミット時の動作
     * @param attributes タグの属性値
     * @param uri サブミット先のURI
     * @param allowDoubleSubmission 二重サブミットを許可するか否か
     * @param requestId サブミット時のリクエストID
     * @param popupWindowName ポップアップのウィンドウ名
     * @param popupOption ポップアップのオプション情報
     * @param displayMethod 表示制御方法
     * @return 新規に発番されたname属性(発番の必要が無い場合はnullを返す。)
     */
    public String addSubmissionInfo(SubmissionAction action, HtmlAttributes attributes , String uri,    // SUPPRESS CHECKSTYLE サブミット情報の生成処理を局所化するため。
                                    boolean allowDoubleSubmission, String requestId,
                                    String popupWindowName, String popupOption,
                                    DisplayMethod displayMethod) {
        String name = TagUtil.escapeHtml(attributes.get(HtmlAttribute.NAME));
        String issuedName = null;
        if (StringUtil.isNullOrEmpty(name) || submissionInfoNames.contains(name)) {
            issuedName = this.name + "_" + (submissionInfoList.size() + 1);
            attributes.put(HtmlAttribute.NAME, issuedName);
            name = issuedName;
        }
        submissionInfoNames.add(name);
        SubmissionInfo info = new SubmissionInfo(
                                  action, name, uri, allowDoubleSubmission,
                                  popupWindowName, popupOption, displayMethod
                              );
        submissionInfoList.add(info);
        requestIds.add(requestId);
        setCurrentSubmissionInfo(info);
        return issuedName;
    }
    
    /**
     * フォームに含まれるサブミット情報を取得する。
     * @return フォームに含まれるサブミット情報
     */
    public List<SubmissionInfo> getSubmissionInfoList() {
        return submissionInfoList;
    }
    
    /**
     * カレントのカレントのサブミット情報を取得する。
     * @return カレントのカレントのサブミット情報
     */
    public SubmissionInfo getCurrentSubmissionInfo() {
        return currentSubmissionInfo;
    }
    
    /**
     * カレントのカレントのサブミット情報を設定する。
     * @param currentSubmissionInfo カレントのカレントのサブミット情報
     */
    public void setCurrentSubmissionInfo(SubmissionInfo currentSubmissionInfo) {
        this.currentSubmissionInfo = currentSubmissionInfo;
    }
    
    /**
     * フォームに含まれるリクエストIDを取得する。
     * @return フォームに含まれるリクエストID
     */
    public List<String> getRequestIds() {
        return requestIds;
    }
    
    /**
     * フォームに含まれるhiddenタグの情報を追加する。
     * @param hiddenTagInfo フォームに含まれるhiddenタグの情報
     */
    public void addHiddenTagInfo(HtmlAttributes hiddenTagInfo) {
        hiddenTagInfoList.add(hiddenTagInfo);
        addInputName(hiddenTagInfo.<String>get(HtmlAttribute.NAME));
    }
    
    /**
     * フォームに含まれるhiddenタグの情報を追加する。
     * @param name name属性の値
     * @param values value属性の値
     */
    public void addHiddenTagInfo(String name, String... values) {
        for (String value : values) {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, name);
            hiddenTagInfo.put(HtmlAttribute.VALUE, value);
            addHiddenTagInfo(hiddenTagInfo);
        }
    }
    
    /**
     * フォームに含まれるhiddenタグの情報を取得する。
     * @return フォームに含まれるhiddenタグの情報
     */
    public List<HtmlAttributes> getHiddenTagInfoList() {
        return hiddenTagInfoList;
    }

    /**
     * 指定されたname属性の値を持つhiddenタグの情報を取得する。
     * <p/>
     * 一番最初に見つかったhiddenタグの情報を返す。
     * 
     * @param name name属性の値
     * @return 指定されたname属性の値を持つhiddenタグの情報。存在しない場合はnull
     */
    public HtmlAttributes getHiddenTagInfo(String name) {
        for (HtmlAttributes hiddenTagInfo : hiddenTagInfoList) {
            if (hiddenTagInfo.get(HtmlAttribute.NAME).equals(name)) {
                return hiddenTagInfo;
            }
        }
        return null;
    }
}
