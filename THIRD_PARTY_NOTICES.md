# Source and license notes

`src/main/java/ch/fmartin/Ascii.java` preserves the search algorithm used in
François Martin's 2019 case-insensitive search experiment. That algorithm came
from his [Guava pull request #3023](https://github.com/google/guava/pull/3023).
The corresponding [Guava source](https://github.com/martinfrancois/guava/blob/c9dfbab4a8886bcea76330c649872709021a8599/guava/src/com/google/common/base/Ascii.java)
carries copyright 2010 The Guava Authors and the Apache License, Version 2.0.
This copy retains the search methods, changes the package and class visibility,
and removes unused prefix/suffix methods. Its file header records these changes.

The newly written benchmark, fixtures, comparison wrappers and tests are
copyright 2026 François Martin, licensed under the
[Apache License, Version 2.0](licenses/Apache-2.0.txt).
The inherited JMH template material retains its existing notices, including
Oracle's BSD-style notice in `pom.xml`. Dependencies retain their own licenses.
This notice and the Apache license are included under `META-INF` in the built JAR.
