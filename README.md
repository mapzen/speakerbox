# Speakerbox

[![Circle CI Build Status](https://circleci.com/gh/mapzen/speakerbox.png?circle-token=3ce51f87eb64cdbdd087e6a2811d3834fb48d714)](https://circleci.com/gh/mapzen/speakerbox)

Android Text-to-Speech simplified.

## Usage

Speakerbox simplifies basic [TextToSpeech][1] tasks and adds new tools including muting and text substitution.

#### Initialization

Speakerbox creates and manages a new instance of [TextToSpeech][1] for a given activity.

```java
Speakerbox speakerbox = new Speakerbox(activity);
```

There is no need to create your own [OnInitListener][2]. If you pass text to Speakerbox before the [TextToSpeech][1] engine has finished initializing it will save the text and automatically play it once initialization is complete.

#### Play

Synthesizing text to speech with Speakerbox is simple. The text will play immediately or once initialization is complete (see above).

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

Speakerbox will automatically shutdown the [TextToSpeech][1] engine and release its resources when the host activity is destroyed so you don't have to.

## Install

#### Download Jar

Download the [latest JAR][3].

#### Maven

Include dependency using Maven.

```xml
<dependency>
  <groupId>com.mapzen.android</groupId>
  <artifactId>speakerbox</artifactId>
  <version>1.4.1</version>
</dependency>
```

#### Gradle

Include dependency using Gradle.

```groovy
compile 'com.mapzen.android:speakerbox:1.4.1'
```

[1]: https://developer.android.com/reference/android/speech/tts/TextToSpeech.html
[2]: https://developer.android.com/reference/android/speech/tts/TextToSpeech.OnInitListener.html
[3]: http://search.maven.org/remotecontent?filepath=com/mapzen/android/speakerbox/1.4.1/speakerbox-1.4.1.jar
