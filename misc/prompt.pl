#!/usr/bin/perl
#----------------------------------------------------------------------#
# Source code by Alvin Alexander, devdaily.com
#----------------------------------------------------------------------#

#------------------------------------------#
#  "driver" program to test &promptUser()  #
#------------------------------------------#

#@parms = &promptUser("SimilarityThresholds", "0.2:0.5:0.8");
#
#for ($i = 0; $i < $#parms + 1; $i++) {
#	print $i . "\t" . $parms[$i] . "\n";
#}

#----------------------------(  promptUser  )-----------------------------#
#                                                                         #
#  FUNCTION:	promptUser                                                #
#                                                                         #
#  PURPOSE:	Prompt the user for some type of input, and return the    #
#		input back to the calling program.                        #
#                                                                         #
#  ARGS:	$promptString - what you want to prompt the user with     #
#		$defaultValue - (optional) a default value for the prompt #
#                                                                         #
#-------------------------------------------------------------------------#

sub getArrayString {
	local @inArray = @_;
	$retString = "";
	for ($i = 0; $i < $#inArray; $i++) {
		$retString .= $inArray[$i] . "-";
	}
	$retString .= $inArray[$#inArray];
	return $retString;
}

sub promptUser {

   #-------------------------------------------------------------------#
   #  two possible input arguments - $promptString, and $defaultValue  #
   #  make the input arguments local variables.                        #
   #-------------------------------------------------------------------#

   local($promptString,$defaultValue) = @_;

   #-------------------------------------------------------------------#
   #  if there is a default value, use the first print statement; if   #
   #  no default is provided, print the second string.                 #
   #-------------------------------------------------------------------#

   if ($defaultValue) {
      print $promptString, " [", $defaultValue, "]: ";
   } else {
      print $promptString, ": ";
   }

   $| = 1;               # force a flush after our print
   $_ = <STDIN>;         # get the input from STDIN (presumably the keyboard)


   #------------------------------------------------------------------#
   # remove the newline character from the end of the input the user  #
   # gave us.                                                         #
   #------------------------------------------------------------------#

   chomp;

   #-----------------------------------------------------------------#
   #  if we had a $default value, and the user gave us input, then   #
   #  return the input; if we had a default, and they gave us no     #
   #  no input, return the $defaultValue.                            #
   #                                                                 # 
   #  if we did not have a default value, then just return whatever  #
   #  the user gave us.  if they just hit the <enter> key,           #
   #  the calling routine will have to deal with that.               #
   #-----------------------------------------------------------------#

   $returnArrayString = ();
   if ("$defaultValue") {
      if ($_) {
      	$returnArrayString = $_;
      } else {
      	$returnArrayString = $defaultValue;
      }
   } else {
      $returnArrayString = $_;
   }
   return split(/:/, $returnArrayString);
}

return 1;
