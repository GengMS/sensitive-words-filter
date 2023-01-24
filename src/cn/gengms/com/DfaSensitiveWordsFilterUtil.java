package cn.gengms.com;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DfaSensitiveWordsFilterUtil {
    private static final String HTML_HIGHLIGHT = "<font color='red'>%s</font>";

    private static final DfaSensitiveWordsFilterExecutor EXECUTOR = DfaSensitiveWordsFilterExecutor.getInstance();

    public static void initOrRefreshExecutor(){
        EXECUTOR.init();
    }


    public static boolean contains(boolean partMatch, String content)  {

        return EXECUTOR.processor(partMatch, content, new DfaSensitiveWordsFilterExecutor.Callback() {
            @Override
            public boolean call(String word) {
                return true; // 有敏感词立即返回
            }
        });
    }

    public static Set<String> getWords(boolean partMatch, String content) throws RuntimeException {
        final Set<String> words = new HashSet<>();

         EXECUTOR.processor(partMatch, content, new DfaSensitiveWordsFilterExecutor.Callback() {
            @Override
            public boolean call(String word) {
                words.add(word);
                return false; // 继续匹配后面的敏感词
            }
        });

        return words;
    }

    public static String highlight(boolean partMatch, String content) throws RuntimeException {
        Set<String> words = getWords(partMatch, content);

        Iterator<String> iter = words.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            content = content.replaceAll(word, String.format(HTML_HIGHLIGHT, word));
        }

        return content;
    }

    public static String filter(boolean partMatch, String content, char replaceChar) throws RuntimeException {
        Set<String> words = getWords(partMatch, content);

        Iterator<String> iter = words.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            String afterFilterString = String.format("%0" + word.length() + "d", 0).replace('0', replaceChar);
            content = content.replaceAll(word, afterFilterString);
        }
        return content;
    }
}
