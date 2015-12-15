Rundeck Tag Orchestrator Plugin
===============================

This plugins allow to group nodes by a given tag in rundeck and orchestrate jobs by groups.

It allow to execute jobs on many nodes (5k in our infra) without being annoyed by one server not being reachable, while being still safe and respect the functional groups of your infrastructure.

Usage in a project
------------------

- Choose a _thread count_ high enough (this is not what will decide your concurrency anymore)
- Choose `tag-orchestrator` as the project _orchestrator_ ("Dispatch to Nodes" option must be choosen)
- Fill the necessary options _TagName_ and _MaxPerGroup_ according to their description.
- Tick the _StopProcessingGroupAfterTooManyFailure_ box if you want to be on the safe side

Options description
-------------------

* _TagName_ describe the name of the tag used to group nodes. Using `osName` will group your nodes by OSes and apply the job concurrently between those groups. You can select multiple group criteria by writing multiple tags.
* _MaxPerGroup_ will decide of the concurrency level inside each group. Value between 0 and 1 will be interpreted as a ratio (between 0% and 100%) of nodes that could run the job simultaneously.
* _StopProcessingGroupAfterTooManyFailure_ is the safety feature. If job execution fails on a _MaxPerGroup_ nodes, the job will stop for that node. This is a better implementation of the _If a node fails_ option in rundeck (which is binary).


How To install
--------------

- Build the package or download a release from maven central (TODO: link).
- Put the jar `$RDECK_BASE/libext`, no rundeck restart should be necessary.


How to build
------------

Use maven: `mvn package`, the artifact will be located in `target` directory.

Contribute
----------

- Fork the repository
- Write a patch with proper unit tests
- Submit a pull request and ask for feedback

It is important to squash your commit into atomic and logical changes. Avoid commit title such as "fixup" or "implement review advices".

License
-------

Apache Software License, Version 2.0. See LICENSE file or http://www.apache.org/licenses/LICENSE-2.0
