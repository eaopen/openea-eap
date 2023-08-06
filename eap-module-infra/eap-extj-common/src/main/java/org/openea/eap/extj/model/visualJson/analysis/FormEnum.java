package org.openea.eap.extj.model.visualJson.analysis;

import java.util.ArrayList;
import java.util.List;

public enum FormEnum {
    table("table"),
    mast("mast"),
    mastTable("mastTable"),
    row("row"),
    collapse("collapse"),
    collapseItem("collapseItem"),
    tab("tab"),
    tabItem("tabItem"),
    tableGrid("tableGrid"),
    tableGridTr("tableGridTr"),
    tableGridTd("tableGridTd"),
    card("card"),
    groupTitle("groupTitle"),
    divider("divider"),
    JNPFText("JNPFText"),
    button("button"),
    relationFormAttr("relationFormAttr"),
    popupAttr("popupAttr"),
    BARCODE("barcode"),
    link("link"),
    alert("alert"),
    QR_CODE("qrcode");

    private String message;
    private static List<String> isNodeList = new ArrayList<String>() {
        {
            this.add(FormEnum.groupTitle.getMessage());
            this.add(FormEnum.divider.getMessage());
            this.add(FormEnum.JNPFText.getMessage());
            this.add(FormEnum.button.getMessage());
            this.add(FormEnum.BARCODE.getMessage());
            this.add(FormEnum.QR_CODE.getMessage());
            this.add(FormEnum.alert.getMessage());
            this.add(FormEnum.link.getMessage());
        }
    };

    private FormEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static boolean isModel(String value) {
        boolean isData = isNodeList.contains(value);
        return isData;
    }
}
