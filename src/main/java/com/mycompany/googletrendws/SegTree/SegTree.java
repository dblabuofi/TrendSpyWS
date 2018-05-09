/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.SegTree;

import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import redis.clients.jedis.Jedis;

/**
 *
 * @author mou1609
 */
public class SegTree {
    SegmentTreeNode root;

    public SegmentTreeNode getRoot() {
        return root;
    }
    
    public SegTree(LocalDate start, LocalDate end) {
        root = build(start, end);
    }

    static public SegmentTreeNode build(LocalDate start, LocalDate end) {
        long days = start.until(end, ChronoUnit.DAYS);
        if (days < 0) {
            return null;
        }
        SegmentTreeNode root = new SegmentTreeNode(start, end);
        if (!start.equals(end)) {
            LocalDate mid = start.plusDays(days / 2);
            root.left = build(start, mid);
            root.right = build(mid.plusDays(1), end);
        }
        return root;
    }
    
    static public void print(SegmentTreeNode root) {
        if (root.start.equals(root.end)) {
            System.out.println(root.start);
        }
//        long days = root.start.until(root.end, ChronoUnit.DAYS);
//        System.out.println(days);
        if (root.left != null)
        print(root.left);
        if (root.right != null)
        print(root.right);
    }
    
    //do we need this??
    static public void modify(SegmentTreeNode root, LocalDate index, int value) {
        if (root.start.equals(index) && root.end.equals(index)) {
            return;
        }
        long days = root.start.until(root.end, ChronoUnit.DAYS);
        LocalDate mid = root.start.plusDays(days / 2);
        if (index.isBefore(mid)) {
            modify(root.left, index, value);
        } else {
            modify(root.right, index, value);
        }
    }

    static public void query(List<Double> res, 
            SegmentTreeNode root, 
            LocalDate start, 
            LocalDate end, 
            String key, 
            Jedis jedis, 
            Random random, 
            Integer range) {
        if (root.start.equals(end)) {
            if (jedis.exists(key + ":" + start)) {
                res.add(Double.valueOf(jedis.get(key + ":" + start)));
                return;
            } else {
                Double value = Double.valueOf(random.nextInt(range));
                jedis.set(key + ":" + start, value + "");
                res.add(value);
                return ;
            }
        }
        long days = root.start.until(root.end, ChronoUnit.DAYS);
        LocalDate mid = root.start.plusDays(days / 2);

        if (start.isBefore(mid) || start.equals(mid)) {//remember this one !!
            if (mid.isBefore(end)) { // ?? 
                query(res, root.left, start, mid, key, jedis, random, range);
            } else { // ?? 
                query(res, root.left, start, end, key, jedis, random, range);
            }
        }
        if (mid.isBefore(end)) { // remember this one !! not else if 
            if (start.isBefore(mid) || start.equals(mid)) {
                query(res, root.right, mid.plusDays(1), end, key, jedis, random, range);
            } else { //  ?? 
                query(res, root.right, start, end, key, jedis, random, range);
            }
        }
    }
}

class SegmentTreeNode {

    public LocalDate start, end;
    public SegmentTreeNode left, right;

    public SegmentTreeNode(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
        this.left = this.right = null;
    }
}
