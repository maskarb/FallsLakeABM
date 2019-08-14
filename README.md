# MASON

## Dependencies

* [R](http://archive.linux.duke.edu/cran/)
* [VineCopula](https://cran.r-project.org/web/packages/VineCopula/index.html) (R package)

Install VineCopula from an R terminal. Type the following command:

    install.packages("VineCopula")

VineCopula is a large package and will take some time to compile and install.


## Coupling R and Java:

### i.e. getting VineCopulas working:

In `src/pertubing/GenerateTimeseries.java`, update `lines 20-33` depending on the OS used. The code committed here should work on Linux so long as `Rscript` is found in `/usr/bin/`. To find the location of Rscript, use:

    which Rscript

in the terminal. Replace `line 25` with whatever is output by the above command. On Windows, the forward slashes (`/`) should be replaced with double backslash (`\\`)

#### For Linux/ maybe Mac OSX:

    String floc = System.getProperty("user.dir");
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
                      + manage
                      + " "
                      + shifacStr
                      + " "
                      + Integer.toString(RunNum));



#### For Windows:

If using this program on Windows, overwrite `lines 20-35` with the following code. Change `line 26` to match your path location of `Rscript.exe`.

    String floc = System.getProperty("user.dir");
    System.out.println(floc + "\\analysis_flows.R ");
    try {
      Process child =
          Runtime.getRuntime()
              .exec(
                  "C:\\R\\R-3.6.1\\bin\\Rscript "
                      + floc
                      + "\\analysis_flows.R "
                      + Long.toString(seed)
                      + " "
                      + manage
                      + " "
                      + shifacStr
                      + " "
                      + Integer.toString(RunNum));


## Running the ABM:

The starting parameters for the ABM can be found on `lines 607-609` in `WRRSim.java`.

    numOfShifts = 8; // 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.9, 1.0
    numOfRun = 1;
    numOfManagementScenarios = 1;

`numOfShifts` is converted to the target shift factor.
`numOfRun` is how many runs of each scenario will be simulated.
`numOfManagementScenarios` controls which mgmt scenarios will be run.

To run only 1 shift factor or 1 management scenario, be sure to change the value of `m` or `n` on `lines 630-631` to be 1 less than the specified shift factor or management scenario.

Using the above parameters with `m = 7` and `n = 0` will produce a single model simulation with a shift factor of 0.7 and the first management scenario.
