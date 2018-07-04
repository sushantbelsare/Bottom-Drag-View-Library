# Bottom-Drag-View-Library
Bottom Drag View is an android library for creating draggable views.
---
***Gradle:***
Add the following in your root directory:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
Add dependancy:
```
dependencies {
	        implementation 'com.github.sushantbelsare:Bottom-Drag-View-Library:0.1.0'
	}
  ```
  ---
***Maven:***
Add Jitpack Repository:
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  ```
  
Add the dependency:
```
<dependency>
	    <groupId>com.github.sushantbelsare</groupId>
	    <artifactId>Bottom-Drag-View-Library</artifactId>
	    <version>0.1.0</version>
	</dependency>
  ```
  ---
  Attribute | Description
  ---------- | -----------
  app:peekHeight | Defines the shortest visible height for view
  ---
  Method | Description
  ------ | -----------
  setPeekHeight(int peekHeight) | Sets peek height eg. ***bottomView.setPeekHeight(60)***
  isViewExpanded() | Returns True if view is expanded else False
  expandView() | Expands View
  collapseView() | Collapses View
  setDragListener(dragListener listener) | Wires the listener to View
  ---
