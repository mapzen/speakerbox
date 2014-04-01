# Speakerbox

[![Build Status](https://travis-ci.org/mapzen/speakerbox.svg?branch=master)](https://travis-ci.org/mapzen/speakerbox)

Android Text-to-Speech simplified.

## Usage

Speakerbox streamlines and simplifies basic TextToSpeech tasks and adds powerful new functionality.

#### Initializion

Speakerbox creates and manages a new instance of TextToSpeech for a given activity.
```java
Speakerbox speakerbox = new Speakerbox(activity);
```

There is no need to create your own OnInitListener. If you pass text to speakerbox before the TextToSpeech engine is initialized it will play the text once initialziation is complete.

#### Play

Sythesizing text to speech with Speakerbox is simple. The text will play immediately or once initializion is complete (see above).

```java
Speakerbox speakerbox = new Speakerbox(activity);
speakerbox.play("Hi");

// Hi
```

#### Mute/Unmute

Speakerbox adds the ability to mute/unmute spoken text.

```java
Speakerbox speakerbox = new Speakerbox(activity);
speakerbox.mute();
speakerbox.play("Quiet please");
speakerbox.unmute();
spearkerbox.play("Cry out loud");

// Cry out loud
```

#### Remix

Substitute spoken text on the fly using the remix feature.

```java
Speakerbox speakerbox = new Speakerbox(activity);
speakerbox.remix("min", "minutes");
speakerbox.play("The show starts in 5 min.");

// The show starts in 5 minutes.
```

#### Shutdown

Speakerbox will automatically shutdown the TextToSpeech engine and release its resources when the host activity is destroyed so you don't have to.

## Install

#### Download Jar

Download the latest JAR.

#### Maven

Include dependency using Maven.

```xml
<dependency>
  <groupId>com.mapzen.android</groupId>
  <artifactId>speakerbox</artifactId>
  <version>1.0.0</version>
</dependency>
```

#### Gradle

Include dependency using Gradle.

```groovy
compile 'com.mapzen.android:speakerbox:1.0.0'
```

## License

```
Copyright 2014 Mapzen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
