%BalancePlayer
x = [73, 1, 507, 212, 262, 368, 838, 134, 1206]; 

%HighScoreWordPlayer
%y = [9, 1000, 954, 979, 975, 960, 915, 988, 866];

z = [0:8];
subplot(1,1,1);
plot(z,x);
%hold on
%plot(z,y);
hold off
h_legend=legend('BalanceOnRackPlayer');
set(h_legend,'FontSize',14);
%legend('BalanceOnRackPlayer');
xlabel('Vowels', 'FontSize',14);
ylabel('Wins', 'FontSize',14);
