# more-collections
*An extension of the Kotlin Collections Framework*

[![JitPack status](https://jitpack.io/v/aeckar/more-collections.svg)](https://jitpack.io/#aeckar/more-collections)
[![Java CI with Gradle](https://github.com/aeckar/more-collections/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/aeckar/more-collections/actions/workflows/gradle-ci.yml)
![GitHub Pages deployment](https://github.com/aeckar/more-collections/actions/workflows/pages/pages-build-deployment/badge.svg?branch=main)
![coverage badge](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/aeckar/712f17b748ca93094db02082fdd86e86/raw/more-collections-coverage.json)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.aeckar:more-collections:1.0.1")
}
```

Started as a utility library for my [parser-functions](https://github.com/aeckar/parser-functions) project,
this library builds upon the Kotlin Collections Framework by providing:

- Multi-sets
- Rich iterators
- Read-only views over collections
- Linked lists
- Trees, and
- Lists of unboxed values

among other utilities. The overall structure of the API is designed to be similar to that of the standard library,
allowing for seamless integration into existing codebases. It also holds no dependencies aside from the JDK.

The full online documentation can be found [here](https://aeckar.github.io/more-collections/).