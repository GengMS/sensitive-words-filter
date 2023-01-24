package cn.gengms.com;

import java.util.HashMap;
import java.util.Map;

public class DfaNode {
    private char _char;
    private DfaNode parent;
    private boolean word;
    private Map<Character, DfaNode> childs;

    public DfaNode(char _char, DfaNode parent, boolean word, Map<Character, DfaNode> childs) {
        this._char = _char;
        this.parent = parent;
        this.word = word;
        this.childs = childs;
    }


    public DfaNode(char _char) {
        this._char = _char;
    }
    public char get_char() {
        return _char;
    }

    public void set_char(char _char) {
        this._char = _char;
    }

    public DfaNode getParent() {
        return parent;
    }

    public void setParent(DfaNode parent) {
        this.parent = parent;
    }

    public boolean isWord() {
        return word;
    }

    public void setWord(boolean word) {
        this.word = word;
    }

    public Map<Character, DfaNode> getChilds() {
        return childs;
    }

    public void setChilds(Map<Character, DfaNode> childs) {
        this.childs = childs;
    }
    public boolean isLeaf() {
        return (childs == null || childs.isEmpty());
    }

    public void addChild(DfaNode child) {
        if (this.childs == null) {
            childs = new HashMap<>();
        }

        this.childs.put(child.get_char(), child);
        //child.setParent(this);
    }
}
