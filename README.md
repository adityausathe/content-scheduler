# Content(Hierarchical) Scheduler

### :heavy_check_mark: Working

## Introduction
Certain scheduling problems can be solved effectively if we view the schedulable entities as a hierarchy rather than a flat sequence. This view of things also opens up avenues to express new kinds of optimization constraints. 

For instance, one of such problems is the scheduling of course-work to prepare a study-schedule. The course-work can be viewed as a hierarchy- built of subjects, which in turn is hierarchical-entity composed of chapters, and so on. It seems natural to put certain restrictions(like time-allocated, weightage-assigned, etc) for a subject or a chapter, and would expect the scheduler to honor those restrictions, at the same level(chapter-level) as well as the levels below it(sub-chapter-level). 

This scheduling framework is designed and implemented to tackle such problems. 

## Functionality
- The scheduling framework hosts the scheduling algorithm along with content and calendar management features, since the algorithm works closely with these.
- The framework offers APIs for scheduling, content-management and calendar-management operations, it also exposes service-provider-interfaces(for persistence layer) to be implemented by the client.
- The repository also contains an android-application client which uses the scheduling framework. It is intended for exposing the scheduling functionality to the end-users by offering them necessary UI.

## Scheduling Algorithm
Documentation to be added

## Implementation
- Most of the domain-related operations are implemented by the framework; the clients would typically deal with data ingestion and persistence.
- The implemented client-app currently only provides persistence layer for the framework; data ingestion via UI is yet to be done.

## Dependencies
- scheduling-framework: Java 9+
- android-app: Android Platform with API-level >= 25
