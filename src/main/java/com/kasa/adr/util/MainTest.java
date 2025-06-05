package com.kasa.adr.util;

public class MainTest {

    public static void main(String[] args) {
        String htmlTemplateTxt = "Please click the below link to choose arbitrator: \\n ${link}";
        String link = "https://virturesolve360.com/assign-random-arbitrator?token=123";// + CommonUtils.createBase64encodeToken(aCase.getId());
        String replace = htmlTemplateTxt.replace("${link}", link);
        System.out.println(replace);
    }
}
