package com.adus.contentscheduler.scheduling;

abstract class TimeAllocationStrategy {
    final SchedulableContent schedulableContent;

    TimeAllocationStrategy(SchedulableContent rootSchedulableContent) {
        this.schedulableContent = rootSchedulableContent;
    }

    abstract void allocate(float availableTime);

    static class UnrestrictedTimeAllocationStrategy extends TimeAllocationStrategy {

        UnrestrictedTimeAllocationStrategy(SchedulableContent rootSchedulableContent) {
            super(rootSchedulableContent);
        }

        @Override
        void allocate(float availableTime) {
            schedulableContent.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(schedulableContent, availableTime));
        }
    }

    static class TimeRestrictionsAwareTimeAllocationStrategy extends TimeAllocationStrategy {

        TimeRestrictionsAwareTimeAllocationStrategy(SchedulableContent rootSchedulableContent) {
            super(rootSchedulableContent);
        }

        @Override
        void allocate(float availableTime) {
            schedulableContent.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(schedulableContent, availableTime));
            schedulableContent.propagateUnrestrictedTimeComponent();
            schedulableContent.adjustResidue();
            schedulableContent.synchronizeStudyTimes();
        }
    }
}
