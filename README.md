# GridStack Add-on for Vaadin 7.5+

This add-on provides GridStackLayout layout component that uses [gridstack.js](https://github.com/troolee/gridstack.js) on client side.

[![Build Status](http://siika.fi:8888/jenkins/job/GridStack%20(Vaadin)/badge/icon)](http://siika.fi:8888/jenkins/job/GridStack%20(Vaadin)/)

## Code example

As code is in experimental stage, please check the DemoUI class.

## Online demo

Try the add-on demo at http://app.siika.fi/GridStackDemo

## FAQ

### More than 8 columns in GridStackLayout are not sized correctly
As part of sizing is done in CSS, in case of more than 8 columns you must:

Add unique stylename to your layout:

```java
GridStackLayout gridstack = new GridStackLayout(12);
gridstack.addStyleName("my-gridstacklayout");
```

Then in scss of your theme, add rule:

```scss
.grid-stack.my-gridstacklayout {
   @include gridstacklayout-columns(12);
}
```

After this you should have all 12 columns sized correctly to take 8.333...% of width.

### Using separate dragging handle
You can define if dragging is done from separate dragging handle or from the whole component. Using the whole child
component as dragging handle can have issues with some components (eg. Buttons).

```java
// Adding component with dragging handle (default behavior)
gridstack.addComponent(myLayout);

// Adding component without dragging handle (content acts as dragging handle)
gridstack.addComponent(myLabel, false);
```

### Using Button without separate dragging handle
Replace your Buttons with GridStackButtons. This modifies client side implementation to prevent dragging issues.
 
```java
Button button = new GridStackButton("My Button");
//... continue using as normal button ... 
```

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/gridstack

## Release notes

### Version 0.4.0 (2017-03-14)
- Ready event has now easy way to verify it's called first time for that GridStackLayout
- Vaadin 8 supported and required
- gridstack.js updated to 0.3.0-dev
- Found out that min-height: 100%; is **bad** idea to have on grid-stack element, so removed it from demo app. Now resizing and dragging works better.
- Move to use Vaadin Icons, as it's part of Valo in Vaadin 8
- Minor fixes done in initial rendering

### Version 0.3.2 (2016-08-07)
- Uses CamelCase methods and variables, to avoid deprecated warnings on client side
- Fix initialization issue when adding GridStack to parent existing on client
- Improving performance and error management in add-on's code
- With complex DOM structures in children gridstack.js initializing gets slow. To fight this ready listener interface
is added. It seams to be faster to add children after gridstack.js has initialized itself, than adding those before
initialization. To use this trick add GrisStackReadyListener and add child components only when it's called. As extra
benefit event will tell the width of component on client side. This can used to adjust the layout.

### Version 0.3.1 (2016-07-20)
- isAreaEmpty will now return false if area goes outside the right edge of layout
- Client side now calls gridstack.js for it's children based on order of coordinates. Issue #18

### Version 0.3.0 (2016-05-18)
- Possibility to move and resize already added children
- Adds animate option to allow nice transitions when moving children
- gridstack.js updated to 0.2.5
- Deprecated API removed
- Fix error in getComponent method with coordinates (could have returned wrong components)

### Version 0.2.1 (2015-12-23)
- Add missing API to define cell height, min width and vertical margins
- API to set wrappers un-scrollable
- GridStackButton component added if you want to use Button without separate draghandle

### Version 0.2.0 (2015-11-30)
- Fixing issue #3 "Removing and subsequently adding Components"
- Clearning unneeded error debug prints on client side
- Way Vaadin components handle events cause issues with gridstack's drag handling. For this reason separate drag handle element is now used by default. Issue #2 
- Adding top level static option to toggle layout's edit mode. Currently still allows to drag children that allow dragging without handle (KNOWN ISSUE).

### Version 0.1.0 (2015-11-09)
- Initial release. Not all features supported yet, but should allow basic usage

## Roadmap
- Touch device support will require extra dependency and testing

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases:
- Adding missing functionality of gridstack.js to server side API

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## Building and running demo

git clone https://github.com/alump/GridStack.git
mvn clean install
cd demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## Licenses & Authors

### Vaadin add-on and integration
Apache License 2.0, Sami Viitanen, http://github.com/alump/GridStack

### gridstack.js
MIT License, Pavel Reznikov, https://github.com/troolee/gridstack.js

### lodash.js
https://github.com/lodash/lodash/blob/master/LICENSE, The Dojo Foundation, https://github.com/lodash/lodash

### jQuery UI
MIT License, The jQuery Foundation and other contributors, https://jqueryui.com/

### jQuery
https://github.com/jquery/jquery/blob/master/LICENSE.txt, The jQuery Foundation and other contributors
