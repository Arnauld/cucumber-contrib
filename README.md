cucumber-contrib
================


Maven Local Repository
----------------------

    mvn install:install-file  -Dfile=tmp/ditaa-0.9.jar \
                              -DgroupId=ditaa \
                              -DartifactId=ditaa \
                              -Dversion=0.9\
                              -Dpackaging=jar \
                              -DlocalRepositoryPath=repo/

    mvn install:install-file  -Dfile=tmp/gral-core-0.10.jar \
                              -DgroupId=de.erichseifert \
                              -DartifactId=gral \
                              -Dversion=0.10\
                              -Dpackaging=jar \
                              -DlocalRepositoryPath=repo/

    mvn install:install-file  -Dfile=tmp/VectorGraphics2D-0.9.1.jar \
                              -DgroupId=de.erichseifert \
                              -DartifactId=VectorGraphics2D \
                              -Dversion=0.9.1\
                              -Dpackaging=jar \
                              -DlocalRepositoryPath=repo/

* [Ditaa](http://ditaa.sourceforge.net/#usage)
* [LaTex short math Guide](ftp://ftp.ams.org/ams/doc/amsmath/short-math-guide.pdf)

