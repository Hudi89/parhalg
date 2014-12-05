int main(int argc, const char *const argv[])
{
	if(argc > 2)
	{
		fputs("excessive arguments\n", stderr);
		return 1;
	}
	submain(argc >= 2 ? argv[1] : NULL);
	return 0;
}
