Easy install of our favorite easter-egg!

![konami-code-tutorial](http://choconatos.com/home/wp-content/uploads/2013/11/Konami-Code.jpg)

![JitPack Badge](https://img.shields.io/github/release/thiagokimo/KonamiCode.svg?label=JitPack)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-KonamiCode-green.svg?style=flat)](https://android-arsenal.com/details/1/2202)

##Preview
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=io.kimo.konami)

[![Thumbnail from youtube](http://i.stack.imgur.com/LT3WE.png)](https://www.youtube.com/watch?v=2xbseI3wP_s)


##Inculde in your project
###Gradle

Add the JitPack repository to your build file:

``` groovy
repositories {
    maven {
	    url "https://jitpack.io"
	}
}
```

Add the dependency in the form:

```groovy
dependencies {
    compile 'com.github.thiagokimo:KonamiCode:1.1.1'
}
```

###Maven

If you use Maven, add this into your build file:

``` xml
<repository>
    <id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
```

And them this into your dependencies
``` xml
<dependency>
    <groupId>com.github.thiagokimo</groupId>
    <artifactId>konami-code</artifactId>
	<version>1.1.1</version>
</dependency>
```
##How to use

Add the following code in your Activity:

``` java
new KonamiCode.Builder(context)
        .into(activity-or-fragment-or-view)
        .install();
```

The library injects a [KonamiCodeLayout]() as the first child of your Activity's view. It listens your swipe events without harming
 the touch events of the original view.

For the first part of the Konami code, swipe into the correct directions. If you do it correctly,
an AlertDialog with the buttons **A**, **B** and **START** will appear. Pressing them correctly will
trigger the final callback.

By default a callback with a Toast message will appear. You can customize the final callback by yourself:

``` java
new KonamiCode.Builder(context)
        .into(activity-or-fragment-or-view)
        .withCallback(new KonamiCode.Callback() {
            @Override
            public void onFinish() {
                //whatever
            }
        })
        .install();
```

###Attention
Make sure you add this **AFTER** your view is set, otherwise it won't listen to your swipes. KonamiCode adds a swipe listener into the root view of your Activity.

##Contribuiting

1. Fork it
2. Create your feature/bug-fix branch(`git checkout -b my-new-feature-or-fix`)
3. Commit your changes (`git commit -am 'Add some feature/fix'`)
4. Do your pull-request

##Developed by

* Thiago Rocha - http://kimo.io - <kimo@kimo.io>

##License

    Copyright 2011, 2012 Thiago Rocha

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
