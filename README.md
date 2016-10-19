#Target
Our target was to establish an independent QA for [social network Diaspora](https://wiki.diasporafoundation.org/Main_Page) 
implementing basic functional test automation in the style of a complete black box testing 
(test automation on production version)

#What was done
* analyzed and researched  Diaspora functional

* developed a functional map
    * functional map (general view)
![functional map (general view)](https://cloud.githubusercontent.com/assets/13263036/19514244/8889ce32-95fb-11e6-9f13-5a8101372c89.jpg)
    * functional map (details)
![functional map (general view)](https://cloud.githubusercontent.com/assets/13263036/19514238/83f2047a-95fb-11e6-8ab4-d9bcdebbd014.jpg)
    * [functional map (interactive)](https://drive.google.com/file/d/0B2UFaKOpHq_MRDNxVENfTlZHSFU/view?usp=sharing)
    
* [implemented auto-tests for more than 50% functional](https://docs.google.com/spreadsheets/d/1y8PItP93mGN1DZvUgOK7hSzYoGXnfNOxcuvsUtJlmj8/edit?usp=sharing)
    * 87 out of 108 features are covered by tests
    * given their complexity can be said that more than 50% covered by the functional

* used reporting for tests ([Allure report](http://allure.qatools.ru/))
    * report (general view)
![report (general view)](https://cloud.githubusercontent.com/assets/13263036/19515820/c1126880-9601-11e6-9124-5a6bae6170ba.jpg)
    * report (detailed information about test)
![report (detailed information about test)](https://cloud.githubusercontent.com/assets/13263036/19515822/c695c75c-9601-11e6-898b-941e82fef1a7.jpg)
    * test method code example - according to report
![test method code example - according to report](https://cloud.githubusercontent.com/assets/13263036/19516357/4bd7d7dc-9604-11e6-8a4a-e6d60ae4f340.jpg)
    * [report archive](https://drive.google.com/file/d/0B2UFaKOpHq_MenpYX05idENhajQ/view?usp=sharing)
    
* as a result 
    * [created issues for diaspora](https://github.com/diaspora/diaspora/issues?utf8=%E2%9C%93&q=author%3Ajuliaviluhina)
    * found some approaches for black box testing on production versions
     