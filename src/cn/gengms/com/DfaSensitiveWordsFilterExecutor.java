package cn.gengms.com;

import java.io.*;
import java.util.HashMap;

public class DfaSensitiveWordsFilterExecutor {

    private  HashMap<Character, DfaNode> cacheNodes;

    private static class SingleFactory{
        private static final DfaSensitiveWordsFilterExecutor INSTANCE = new DfaSensitiveWordsFilterExecutor();
    }

    public interface Callback {

        /**
         * 匹配掉敏感词回调
         * @author gms
         * @param word 敏感词
         * @return true 立即停止后续任务并返回，false 继续执行
         */
        boolean call(String word);
    }

    private DfaSensitiveWordsFilterExecutor() {
    }

    public static DfaSensitiveWordsFilterExecutor getInstance(){
        return SingleFactory.INSTANCE;
    }

    public void init(){
        cacheNodes = new HashMap<>();
        BufferedReader reader = null;
        try {

            DfaSensitiveWordsFilterExecutor instance = DfaSensitiveWordsFilterExecutor.getInstance();
            InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getResourceAsStream("/sensitiveWords.txt"));
            reader = new BufferedReader(inputStreamReader);
            String str = null;
            while((str = reader.readLine()) != null){
                //System.out.println(str);//此时str就保存了一行字符串
                if(str.trim().length() == 0){
                    continue;
                }
                instance.put(str.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean put(String word){

        if (word == null) {
            return false;
        }
        if (word.length() < 2) {
            return false;
        }
        Character fisrtChar = word.charAt(0);
        DfaNode node = cacheNodes.get(fisrtChar);
        if (node == null) {
            node = new DfaNode(fisrtChar);
            cacheNodes.put(fisrtChar, node);
        }
        for (int i = 1; i < word.length(); i++) {
            Character nextChar = word.charAt(i);

            DfaNode nextNode = null;
            if (!node.isLeaf()) {
                nextNode = node.getChilds().get(nextChar);
            }
            if (nextNode == null) {
                nextNode = new DfaNode(nextChar);
            }
            node.addChild(nextNode);
            node = nextNode;
            if (i == word.length() - 1) {
                node.setWord(true);
            }
        }
        return true;
    }

    /**
     *
     * @param partMatch 是否全字匹配
     * @param content 内容
     * @param callback 回调
     * @return
     */
    protected boolean processor(boolean partMatch, String content, Callback callback){
        if (content == null) {
            return false;
        }
        if (content.trim().length() < 2) {
            return false;
        }

        for (int index = 0; index < content.length(); index++) {
            char fisrtChar = content.charAt(index);

            DfaNode node = cacheNodes.get(fisrtChar);
            if (node == null || node.isLeaf()) {
                continue;
            }

            int charCount = 1;
            for (int i = index + 1; i < content.length(); i++) {
                char wordChar = content.charAt(i);
                //这个if用来实现 如果敏感词间带有换行，空格等格式可以正常进行匹配， 这个if去掉后也可正常使用，但不能跳过空格等特殊格式; 后期可以再丰富条件
                if(isSkip(wordChar)){
                    charCount++;
                    continue;
                }
                node = node.getChilds().get(wordChar);
                if (node != null) {
                    charCount++;
                } else {
                    break;
                }

                if (partMatch && node.isWord()) {
                    if (callback.call(content.substring(index, index+charCount))) {
                        return true;
                    }
                    break;
                } else if (node.isWord()) {
                    if (callback.call(content.substring(index, index+charCount))) {
                        return true;
                    }
                }

                if (node.isLeaf()) {
                    break;
                }
            }

            if (partMatch) {
                index += charCount;
            }
        }

        return false;
    }

    /**
     *
     * @param wordChar
     * @return
     */
    private boolean isSkip(char wordChar){
        if((Character.isSpaceChar(wordChar) || wordChar == '\n' || "\r\n".indexOf(wordChar) != -1)){
            return true;
        }
        return false;
    }
}
