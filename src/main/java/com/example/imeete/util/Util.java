package com.example.imeete.util;

import com.alibaba.fastjson2.JSONObject;
import java.util.HashSet;
import java.util.Set;

public class Util {
  private static final Set<String> mbti =
      Set.of(
          "ISTJ", "ISFJ", "INFJ", "INTJ", "ISTP", "ISFP", "INFP", "INTP", "ESTP", "ESFP", "ENFP",
          "ENTP", "ESTJ", "ESFJ", "ENFJ", "ENTJ");

  public static JSONObject successResponse(String message) {
    JSONObject res = new JSONObject();
    res.put("ok", true);
    res.put("message", message);
    return res;
  }

  public static JSONObject errorResponse(String message) {
    JSONObject res = new JSONObject();
    res.put("ok", false);
    res.put("message", message);
    return res;
  }

  public static Set<String> getMbtiSet(String mbti) {
    Set<String> set = new HashSet<>();
    if (mbti.equals("NONE")) return set;
    if (mbti.length() == 4) {
      for (String entry : Util.mbti)
        if (entry.contains(mbti.substring(0, 1))
            && entry.contains(mbti.substring(1, 2))
            && entry.contains(mbti.substring(2, 3))
            && entry.contains(mbti.substring(3, 4))) set.add(entry);
    } else if (mbti.length() == 3) {
      for (String entry : Util.mbti)
        if (entry.contains(mbti.substring(0, 1))
            && entry.contains(mbti.substring(1, 2))
            && entry.contains(mbti.substring(2, 3))) set.add(entry);
    } else if (mbti.length() == 2) {
      for (String entry : Util.mbti)
        if (entry.contains(mbti.substring(0, 1)) && entry.contains(mbti.substring(1, 2)))
          set.add(entry);
    } else for (String entry : Util.mbti) if (entry.contains(mbti)) set.add(entry);
    return set;
  }
}
