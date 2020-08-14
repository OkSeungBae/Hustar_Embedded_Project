#include "stm32f4xx_hal.h"
#include "stm32f4xx_it.h"

//UART ���ͷ�Ʈ ISR�Ѥ� ���� UartHandler������ �ܺ����� ������ ����
extern UART_HandleTypeDef UartHandle1, UartHandle2;

void USART1_IRQHandler(void)
{
	HAL_UART_IRQHandler(&UartHandle1);		//UART���ͷ�Ʈ callback�Լ�
}
void USART2_IRQHandler(void)
{
	HAL_UART_IRQHandler(&UartHandle2);		//UART���ͷ�Ʈ callback�Լ�
}
