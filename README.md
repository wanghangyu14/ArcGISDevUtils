### ArcGIS for Android Utilities

#### Instruction:
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        maven {
            url 'https://esri.jfrog.io/artifactory/arcgis'
        }
    }
}
```

Step 2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.wanghangyu14:ArcGISDevUtils:latest-release-version'
}
```