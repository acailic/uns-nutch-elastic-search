'use strict';

/* https://github.com/angular/protractor/blob/master/docs/toc.md */

describe('my app', function() {

  browser.get('index.html');

  it('should automatically redirect to /searchview when location hash/fragment is empty', function() {
    expect(browser.getLocationAbsUrl()).toMatch("/searchview");
  });


  describe('view1', function() {

    beforeEach(function() {
      browser.get('index.html#/searchview');
    });


    it('should render searchview when user navigates to /searchview', function() {
      expect(element.all(by.css('[ng-view] p')).first().getText()).
        toMatch(/partial for view 1/);
    });

  });


  describe('view2', function() {

    beforeEach(function() {
      browser.get('index.html#/configview');
    });


    it('should render configview when user navigates to /configview', function() {
      expect(element.all(by.css('[ng-view] p')).first().getText()).
        toMatch(/partial for view 2/);
    });

  });
});
