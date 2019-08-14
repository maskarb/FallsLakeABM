# MASON

## Dependencies

* [R](http://archive.linux.duke.edu/cran/)
* [VineCopula](https://cran.r-project.org/web/packages/VineCopula/index.html) (R package)

Install VineCopula from an R terminal. Type the following command:

    `install.packages("VineCopula")`

VineCopula is a large package and will take some time to compile and install.



    `String floc = System.getProperty("user.dir");
    System.out.println(floc + "/analysis_flows.R ");
    try {
      Process child =
          Runtime.getRuntime()
              .exec(
                  "/usr/bin/Rscript "
                      + floc
                      + "/analysis_flows.R "
                      + Long.toString(seed)
                      + " "
                      + shifacStr
                      + " "
                      + Integer.toString(RunNum));`