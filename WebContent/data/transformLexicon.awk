{
	split($5,a,"/");
	print $1"\tpolarity="$4"\tprobability="a[2]"\tlemma="$2"\tPOS="$3;
}
