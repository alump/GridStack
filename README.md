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

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/gridstack

## Release notes

### Version 0.1.1 (2015-??)
- TBD

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
