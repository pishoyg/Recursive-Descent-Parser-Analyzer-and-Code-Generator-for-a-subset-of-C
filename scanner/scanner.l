%{
	int yywrap(){return 1;}
%}

letter				[A-Za-z]
digit				[0-9]

integer				{digit}+
fraction			{integer}"."{integer}
number				{fraction}|{integer}
integer_literal			{integer}
real_literal			({fraction}|{number}[eE][+\-]?{number})

opening_comment_operator	"/*"
closing_comment_operator	"*/"
comment_body			([^*]|[*][^/])*
line_comment			"//"[^\n]*
multiline_comment		{opening_comment_operator}{comment_body}{closing_comment_operator}
comment				{multiline_comment}|{line_comment}

addition_operator		[+\-]
multiplication_operator		[*/]
relational_operator		[<>][=]?|[!=][=]
assignment_operator		"="
left_parenthesis		"("
right_parenthesis		")"
left_curly_bracket		"{"
right_curly_bracket		"}"
left_square_bracket		"["
right_square_bracket		"]"
semicolon			";"
comma				","
parenthesis			{left_parenthesis}|{right_parenthesis}
curly_bracket			{left_curly_bracket}|{right_curly_bracket}
square_bracket			{left_square_bracket}|{right_square_bracket}
operator			{arithmetic_operator}|{relational_operator}|{assignment_operator}|{parenthesis}|{curly_bracket}|{square_bracket}|{semicolon}|{comma}

if_keyword			if
else_keyword			else
while_keyword			while
return_keyword			return

void_keyword			void
int_keyword			int
float_keyword			float

identifier			{letter}({letter}|{digit})*([.@_]({letter}|{digit})+)?

space				[ \t]
any_thing_else			.
new_line			[\n]
%%

{integer_literal}		printf("%d->integer_literal->%s\n", yylineno, yytext);
{real_literal}			printf("%d->real_Literal->%s\n", yylineno, yytext);

{new_line}			++yylineno;
{space}				;
{comment}			;
{opening_comment_operator}	printf("%d->error->%s\n", yylineno, yytext);

{addition_operator}		printf("%d->addition_operator->%s\n", yylineno, yytext);
{multiplication_operator}	printf("%d->multiplication_operator->%s\n", yylineno, yytext);
{relational_operator}		printf("%d->relational_operator->%s\n", yylineno, yytext);
{assignment_operator}		printf("%d->assignment_operator->%s\n", yylineno, yytext);
{left_parenthesis}		printf("%d->left_parenthesis->%s\n", yylineno, yytext);
{right_parenthesis}		printf("%d->right_parenthesis->%s\n", yylineno, yytext);
{left_curly_bracket}		printf("%d->left_curly_bracket->%s\n", yylineno, yytext);
{right_curly_bracket}		printf("%d->right_curly_bracket->%s\n", yylineno, yytext);
{left_square_bracket}		printf("%d->left_square_bracket->%s\n", yylineno, yytext);
{right_square_bracket}		printf("%d->right_square_bracket->%s\n", yylineno, yytext);
{semicolon}			printf("%d->semicolon->%s\n", yylineno, yytext);
{comma}				printf("%d->comma->%s\n", yylineno, yytext);

{if_keyword}			printf("%d->if_keyword->%s\n", yylineno, yytext);
{else_keyword}			printf("%d->else_keyword->%s\n", yylineno, yytext);
{while_keyword}			printf("%d->while_keyword->%s\n", yylineno, yytext);
{return_keyword}		printf("%d->return_keyword->%s\n", yylineno, yytext);

{int_keyword}			printf("%d->int_keyword->%s\n", yylineno, yytext);
{void_keyword}			printf("%d->void_keyword->%s\n", yylineno, yytext);
{float_keyword}			printf("%d->float_keyword->%s\n", yylineno, yytext);

{identifier}			printf("%d->identifier->%s\n", yylineno, yytext);

%%
int main(int argc, char *argv[])
{
	yyin = fopen(argv[1],"r+");
	if (argc > 1) freopen(argv[2], "w", stdout);
	yylex();
	return 0;
}

