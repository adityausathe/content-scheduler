package com.adus.contentscheduler.scheduling;

class ResidualTimeAllocator {
    private final float residualTime;
    private final Mode mode;
    private final Float totalAvailableUnRestrictedTime;

    ResidualTimeAllocator(Float residualTime, Float totalAvailableUnRestrictedTime) {
        this.totalAvailableUnRestrictedTime = totalAvailableUnRestrictedTime;
        this.residualTime = residualTime;
        this.mode = (residualTime > 0) ? Mode.CREDIT : Mode.DEBIT;
        if (mode == Mode.DEBIT && totalAvailableUnRestrictedTime < Math.abs(residualTime))
            throw new RuntimeException("Infeasible constraints");
    }

    float allocate(Float availableUnRestrictedTime) {
        if (mode == Mode.CREDIT) {
            return calculateResidueToCredit(availableUnRestrictedTime);
        }
        return calculateResidueToDebit(availableUnRestrictedTime);
    }

    private float calculateResidueToCredit(Float availableUnRestrictedTime) {
        return (1 - (availableUnRestrictedTime / totalAvailableUnRestrictedTime)) * residualTime;
    }

    private float calculateResidueToDebit(Float availableUnRestrictedTime) {
        return (availableUnRestrictedTime / totalAvailableUnRestrictedTime) * residualTime;
    }

    private enum Mode {
        CREDIT, DEBIT
    }
}
