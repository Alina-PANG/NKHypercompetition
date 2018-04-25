#!/usr/bin/perl

use Cwd qw();
require "./prompt.pl";
my $path = Cwd::realpath('..');

# date stamp for shell script to submit jobs
($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
$year = sprintf("%02d", $year % 100);
$mon += 1;
if (length($mday) == 1) { $mday = "0" . $mday; }
$datestamp = $year . $mon . $mday;

@Ns = &promptUser("N: ", "20");
@numFirms = &promptUser("Number of firms: ", "20:50:100");
@numResources = &promptUser("Number of resources at init: ", "5:10:15");
@infmats = &promptUser("Influence matrices: ", "matrix0:matrix3:matrix6:matrix9:matrix12:matrix15");
@iterations = &promptUser("Number of iterations: ", "100");
@adaptations = &promptUser("Firm adaptation method: ", "search:resource");

open (SCRIPT, ">$path/runexp_$datestamp.sh") or die "couldn't open runexp_$datestamp.sh\n";
print SCRIPT "#!/bin/sh\n\n";
close SCRIPT;
print $#Ns . "\t" . $#numResources . "\t" . $#numFirms . "\t" . $#infmats . "\t" . $#iterations . "\t" . $#adaptations . "\n";
for ($n = 0; $n < $#Ns + 1; $n++) {
	for ($nr = 0; $nr < $#numResources + 1; $nr++) {
		for ($nf = 0; $nf < $#numFirms + 1; $nf++) {
			for ($im = 0; $im < $#infmats + 1; $im++) {
				for ($it = 0; $it < $#iterations + 1; $it++) {
					for ($adapt = 0; $adapt < $#adaptations + 1; $adapt++) {
							# print $n . "\t" . $nr . "\t" . $nf . "\t" . $im . "\t" . $it . "\t" . $adapt . "\n";
							&writeconf($datestamp, $path, $Ns[$n], $numResources[$nr], $numFirms[$nf], $infmats[$im], $iterations[$it], $adaptations[$adapt]);
							&writescript($datestamp, $path, $Ns[$n], $numResources[$nr], $numFirms[$nf], $infmats[$im], $iterations[$it], $adaptations[$adapt]);
					}
				}
			}
		
		}
	}	
}
print "Configuring jobs!\n";

sub writeconf {
	my ($stamp, $basedir, $nvalue, $numres, $firms, $inf, $iter, $adpt) = @_;
	my $conffile = $nvalue . "_" . $numres . "_" . $firms . "_" . $inf . "_" . $iter . "_" . $adpt . ".conf";
	my $result = $nvalue . "_" . $numres . "_" . $firms . "_" . $inf . "_" . $iter . "_" . $adpt . ".txt";

	#print "Writing $conffile\n";
	open (CONF, ">$basedir/conf/$conffile") or die "couldn't open $basedir/conf/$conffile\n";
	print CONF "N=$nvalue\n";
	print CONF "numResources=$numres\n";
	print CONF "numFirms=$firms\n";
	print CONF "influenceMatrixFile=$inf\n";
	print CONF "iterations=$iter\n";
	print CONF "adaptation=$adpt\n";
	print CONF "outfile=$result\n";
	close CONF;

}

sub writescript {
	my ($stamp, $basedir, $nvalue, $numres, $firms, $inf, $iter, $adpt) = @_;
	my $conffile = $nvalue . "_" . $numres . "_" . $firms . "_" . $inf . "_" . $iter . "_" . $adpt . ".conf";

	open (SCRIPT, ">>$basedir/runexp_$stamp.sh") or die "couldn't open runexp_$stamp.sh\n";	
	print SCRIPT "/usr/bin/java app.Simulation $basedir/conf/$conffile\n";
	close SCRIPT;
}
1;
