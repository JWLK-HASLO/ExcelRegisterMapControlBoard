clear all; close all; clc;

bit_width = 12;
bit_width_1 = 16;

element_pitch = 312*1e-3; %312um = 312*1e-3mm
N_element = 32;
depth = 40; %2 cm = 20 mm


fileID_1 = fopen('dat_5.txt', 'r');
formatSpec = '%x';
result_mj = fscanf(fileID_1, formatSpec); %88883333
fclose(fileID_1);

I = floor(result_mj/2^16);
Q = result_mj-I*2^16;

I_1 = ( I >= 2^(bit_width_1-1)  ).*(I-2^bit_width_1) + (I < 2^(bit_width_1-1) ).*(I);

Q_1 = (Q >= 2^(bit_width_1-1)).*(Q-2^bit_width_1) + (Q<2^(bit_width_1-1)).*(Q);

mag_vhdl = sqrt(I_1.^2+Q_1.^2);
mag_vhdl = reshape(mag_vhdl,1024,32);

dynamic_range = 60;
mag_vhdl = mag_vhdl/max( mag_vhdl(:) );

logout_1 = 20*log10(mag_vhdl) + dynamic_range;
logout_1 = (logout_1>=0).*logout_1 + (logout_1<0)*0;
logout_1 = logout_1/max(logout_1(:))*255;

view_width = element_pitch*N_element;

x_idx = -view_width/2:element_pitch:view_width/2;
y_idx = 0:depth;

figure(2);
imagesc(x_idx,y_idx, logout_1);
axis image; colormap(gray(256));