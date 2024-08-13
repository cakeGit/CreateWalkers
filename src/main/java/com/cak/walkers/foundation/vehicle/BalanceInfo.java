package com.cak.walkers.foundation.vehicle;

import com.jozufozu.flywheel.util.Pair;
import net.minecraft.core.Direction;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BalanceInfo {
    
    HashMap<Integer, Balance> balancesByOffset = new HashMap<>();
    boolean unstable;
    
    double balanceCenter;
    
    Balance backBalance;
    Balance centerBackBalance;
    Balance centerFrontBalance;
    Balance frontBalance;
    
    public BalanceInfo(Set<Leg> legs, Direction.Axis rotationAxis) {
        for (Leg leg : legs) {
            int balanceOffset = (int) Math.floor(leg.offset.get(rotationAxis));
            
            Balance balance = balancesByOffset.getOrDefault(
                        balanceOffset, new Balance(balanceOffset, new ArrayList<>()));
            balance.legs.add(leg);
            balancesByOffset.put(balanceOffset, balance);
        }
        
        Set<Integer> balancePositions = balancesByOffset.keySet();
        if (balancePositions.size() <= 1) unstable = true;
        
        Integer totalOffset = 0;
        for (Integer offset : balancePositions) totalOffset += offset;
        balanceCenter = totalOffset / ((double) balancePositions.size());
        
        for (Balance balance : balancesByOffset.values()) {
            if (balance.offset == balanceCenter) {
                centerBackBalance = balance;
                centerFrontBalance = balance;
            } else if (balance.offset > balanceCenter) {
                //Front
                if (centerFrontBalance == null || balance.offset < centerFrontBalance.offset)
                    centerFrontBalance = balance;
                
                if (frontBalance == null || balance.offset > frontBalance.offset)
                    frontBalance = balance;
            } else if (balance.offset < balanceCenter) {
                //Back
                if (centerBackBalance == null || balance.offset > centerBackBalance.offset)
                    centerBackBalance = balance;
                
                if (backBalance == null || balance.offset < backBalance.offset)
                    backBalance = balance;
            }
        }
    }
    
    /**Get the extent and center balances, based on direction of the unsupported side*/
    public Pair<Balance, Balance> getBalancePair(Direction.AxisDirection direction) {
        if (direction == Direction.AxisDirection.POSITIVE)
            return Pair.of(frontBalance, centerBackBalance);
        else
            return Pair.of(centerFrontBalance, backBalance);
    }
    
    /**Returns whether to tick this or not, (with all aligned legs there's nothing to balance between)*/
    public boolean isUnstable() {
        return unstable;
    }
    
    public Set<Balance> getAllFrontBalances() {
        return balancesByOffset.values().stream().filter(balance -> balance.offset > balanceCenter).collect(Collectors.toSet());
    }
    
    public Set<Balance> getAllBackBalances() {
        return balancesByOffset.values().stream().filter(balance -> balance.offset < balanceCenter).collect(Collectors.toSet());
    }
    
    public Set<Balance> getAllBalancesOnSide(Direction.AxisDirection direction) {
        if (direction == Direction.AxisDirection.NEGATIVE)
            return getAllBackBalances();
        return getAllFrontBalances();
    }
    
    public record Balance(Integer offset, List<Leg> legs) {
    
    }
    
}
