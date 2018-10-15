package com.qcj.map_reduce;

import java.util.Map;
//抽象出来的接口
public interface Reduce {
    java.util.Map<String,Integer> merge(Map<String,Integer>... maps);
}
