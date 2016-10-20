#Goal
Our goal was to establish an independent QA for [social network Diaspora](https://wiki.diasporafoundation.org/Main_Page) 
implementing basic functional test automation in the style of a complete black box testing 
(test automation on production version)

#"Done" list
* research and analysis of the Diaspora functionality were performed 

* functional map was developed
    * functional map (general view)
![functional map (general view)](https://cloud.githubusercontent.com/assets/13263036/19514244/8889ce32-95fb-11e6-9f13-5a8101372c89.jpg)
    * functional map (details)
![functional map (general view)](https://cloud.githubusercontent.com/assets/13263036/19514238/83f2047a-95fb-11e6-8ab4-d9bcdebbd014.jpg)
    * [functional map (interactive)](https://drive.google.com/file/d/0B2UFaKOpHq_MRDNxVENfTlZHSFU/view?usp=sharing)
    
* [implemented auto-tests for more than 50% functional](https://docs.google.com/spreadsheets/d/1y8PItP93mGN1DZvUgOK7hSzYoGXnfNOxcuvsUtJlmj8/edit?usp=sharing)
    * 87 out of 108 features were covered by tests
    * taking into account the complexity of features, the approximate percentage of coverage is near 50%

* reporting for tests was configured ([Allure report](http://allure.qatools.ru/))
    * report (general view)
![report (general view)](https://cloud.githubusercontent.com/assets/13263036/19515820/c1126880-9601-11e6-9124-5a6bae6170ba.jpg)
    * report (detailed information about test)
![report (detailed information about test)](https://cloud.githubusercontent.com/assets/13263036/19515822/c695c75c-9601-11e6-898b-941e82fef1a7.jpg)
    * test method code example - according to report
![test method code example - according to report](https://cloud.githubusercontent.com/assets/13263036/19516357/4bd7d7dc-9604-11e6-8a4a-e6d60ae4f340.jpg)
    * [report archive](https://drive.google.com/file/d/0B2UFaKOpHq_MenpYX05idENhajQ/view?usp=sharing)
    
* as a result 
    * [16 issues for diaspora was created](https://github.com/diaspora/diaspora/issues?utf8=%E2%9C%93&q=author%3Ajuliaviluhina)
        * (12 out of 16 issues were closed, 6 out of 12 closed issues were resolved as of 19 October 2016)
    * expertise of "black box" approach for Web UI automation of application "production version" was built
      