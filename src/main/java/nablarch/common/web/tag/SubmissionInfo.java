package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.web.tag.ChangeParamNameTag.ChangeParamName;

/**
 * サブミット情報を保持するクラス。
 * @author Kiyohito Itoh
 */
public class SubmissionInfo {
    
    /**
     * サブミット時の動作を表す列挙型。
     * @author Kiyohito Itoh
     */
    public enum SubmissionAction {
        /** 画面遷移 */
        TRANSITION,
        /** ポップアップ */
        POPUP,
        /** ダウンロード */
        DOWNLOAD
    }
    
    /** サブミット時の動作 */
    private final SubmissionAction action;
    
    /** name属性の値 */
    private final String name;
    
    /** サブミット先のURI */
    private final String uri;
    
    /** 二重サブミットを許可するか否か */
    private final boolean allowDoubleSubmission;
    
    /** サブミット時に追加するパラメータ */
    private final Map<String, List<String>> paramsMap;

    /** ポップアップのウィンドウ名 */
    private final String popupWindowName;

    /** ポップアップのオプション情報 */
    private final String popupOption;
    
    /** ポップアップ用のサブミット時のパラメータ名の変更情報 */
    private final List<ChangeParamName> changeParamNames;
    
    /** 表示制御方法 */
    private final DisplayMethod displayMethod;
    
    /**
     * コンストラクタ。
     * @param action サブミット時の動作
     * @param name name属性の値
     * @param uri サブミット先のURI
     * @param allowDoubleSubmission 二重サブミットを許可するか否か
     * @param popupWindowName ポップアップのウィンドウ名
     * @param popupOption ポップアップのオプション情報
     * @param displayMethod 表示制御方法
     */
    public SubmissionInfo(SubmissionAction action, String name, String uri,
                           boolean allowDoubleSubmission,
                           String popupWindowName, String popupOption,
                           DisplayMethod displayMethod) {
        this.action = action;
        this.name = name;
        this.uri = uri;
        this.allowDoubleSubmission = allowDoubleSubmission;
        this.popupWindowName = popupWindowName;
        this.popupOption = popupOption;
        paramsMap = new HashMap<String, List<String>>();
        changeParamNames = new ArrayList<ChangeParamName>();
        this.displayMethod = displayMethod;
    }

    /**
     * name属性の値を取得する。
     * @return name属性の値
     */
    public String getName() {
        return name;
    }

    /**
     * サブミット先のURIを取得する。
     * @return サブミット先のURI
     */
    public String getUri() {
        return uri;
    }

    /**
     * 二重サブミットを許可するか否かを取得する。
     * @return 二重サブミットを許可するか否か
     */
    public boolean isAllowDoubleSubmission() {
        return allowDoubleSubmission;
    }
    
    /**
     * サブミット時の動作を取得する。
     * @return サブミット時の動作
     */
    public SubmissionAction getAction() {
        return action;
    }

    /**
     * ポップアップのウィンドウ名を取得する。
     * @return ポップアップのウィンドウ名
     */
    public String getPopupWindowName() {
        return popupWindowName;
    }

    /**
     * ポップアップのオプション情報を取得する。
     * @return ポップアップのオプション情報
     */
    public String getPopupOption() {
        return popupOption;
    }

    /**
     * サブミット時に追加するパラメータを追加する。
     * @param name パラメータの名前
     * @param value パラメータの値
     */
    public void addParam(String name, String value) {
        if (!paramsMap.containsKey(name)) {
            paramsMap.put(name, new ArrayList<String>());
        }
        paramsMap.get(name).add(value);
    }
    
    /**
     * サブミット時に追加するパラメータを取得する。
     * @return サブミット時に追加するパラメータ
     */
    public Map<String, List<String>> getParamsMap() {
        return paramsMap;
    }
    
    /**
     * サブミット時に追加するパラメータをクリアする。
     */
    public void clearParams() {
        paramsMap.clear();
    }
    
    /**
     * ポップアップ用のサブミット時のパラメータ名の変更情報を追加する。
     * @param paramName サブミット時に使用するパラメータの名前
     * @param inputName 変更元となる元画面のinput要素のname属性
     */
    public void addChangeParamName(String paramName, String inputName) {
        changeParamNames.add(new ChangeParamName(paramName, inputName));
    }

    /**
     * ポップアップ用のサブミット時のパラメータ名の変更情報を取得する。
     * @return ポップアップ用のサブミット時のパラメータ名の変更情報
     */
    public List<ChangeParamName> getChangeParamNames() {
        return changeParamNames;
    }
    
    /**
     * 該当するサブミットタグの表示方法を取得する。
     * 
     * @return 該当するサブミットタグの表示方法
     */
    public DisplayMethod getDisplayMethod() {
        return displayMethod;
    }
}
